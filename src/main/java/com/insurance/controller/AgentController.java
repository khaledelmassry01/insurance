package com.insurance.controller;

import com.insurance.model.Agent;
import com.insurance.repository.AgentRepository;
import com.insurance.security.AgentUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
@SuppressWarnings("null")
public class AgentController {

    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);
    private final AgentRepository agentRepository;

    public AgentController(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    private Integer getAgentIdFromAuth(Authentication auth) {
        Object principal = auth.getPrincipal();

        // If principal is AgentUserDetails, get agentId directly
        if (principal instanceof AgentUserDetails) {
            return ((AgentUserDetails) principal).getAgentId();
        }
        
        // If principal is String, it should be the agentId from JWT subject
        if (principal instanceof String) {
            try {
                return Integer.parseInt((String) principal);
            } catch (NumberFormatException e) {
                logger.error("Could not parse agentId from principal: {}", principal);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid agent ID in token");
            }
        }
        
        // If principal is UserDetails but not AgentUserDetails, try to get from username
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            Agent agent = agentRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("No agent found for username: {}", username);
                    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Agent details not found for authenticated user");
                });
            return agent.getAgentId();
        }

        logger.error("Cannot determine agentId from principal of type: {}", principal.getClass().getName());
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not determine agentId from authentication principal");
    }

    @GetMapping
    public ResponseEntity<List<Agent>> getAllAgents(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));
        Integer agentId = getAgentIdFromAuth(auth);
        
        List<Agent> agents = isAdmin
            ? agentRepository.findAll()
            : agentRepository.findById(agentId).stream().toList();
            
        return ResponseEntity.ok(agents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agent> getAgentById(@PathVariable Integer id, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));
        Integer agentId = getAgentIdFromAuth(auth);
        
        if (!isAdmin && !id.equals(agentId)) {
            return ResponseEntity.status(403).build();
        }
        
        return agentRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ جلب وكيل باستخدام البريد الإلكتروني (ADMIN only)
    @GetMapping("/by-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Agent> getAgentByEmail(@RequestParam String email) {
        return agentRepository.findByEmail(email)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ إنشاء وكيل جديد (ADMIN only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Agent> createAgent(@RequestBody Agent agent) {
        if (agent.getRole() == null) {
            agent.setRole("AGENT");
        }
        return ResponseEntity.ok(agentRepository.save(agent));
    }

    // ✅ تعديل وكيل موجود (ADMIN only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Agent> updateAgent(@PathVariable Integer id, @RequestBody Agent agent) {
        if (!agentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        agent.setAgentId(id);
        return ResponseEntity.ok(agentRepository.save(agent));
    }

    // ✅ حذف وكيل (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAgent(@PathVariable Integer id) {
        if (!agentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        agentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
