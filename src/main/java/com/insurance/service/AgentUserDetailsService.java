package com.insurance.service;

import com.insurance.model.Agent;
import com.insurance.repository.AgentRepository;
import com.insurance.security.AgentUserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("agentUserDetailsService")
public class AgentUserDetailsService implements UserDetailsService {

    private final AgentRepository agentRepository;

    public AgentUserDetailsService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Agent agent = agentRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Agent not found with email: " + email));

        String roleName = "ROLE_" + agent.getRole().toUpperCase();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName);

        return new AgentUserDetails(agent, List.of(authority));
    }

    /**
     * تحميل تفاصيل الوكيل بناءً على agentId لقراءة الـ JWT
     */
    public AgentUserDetails loadByAgentId(int agentId) throws UsernameNotFoundException {
        Agent agent = agentRepository.findById(agentId)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Agent not found with id: " + agentId));

        String roleName = "ROLE_" + agent.getRole().toUpperCase();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName);

        return new AgentUserDetails(agent, List.of(authority));
    }
}
