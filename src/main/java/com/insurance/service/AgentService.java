package com.insurance.service;

import com.insurance.model.Agent;
import com.insurance.repository.AgentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@SuppressWarnings("null")
public class AgentService {
    
    private final AgentRepository agentRepository;
    
    
    public AgentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }
    
    public List<Agent> getAllAgents() {
        return agentRepository.findAll();
    }
    
    public Optional<Agent> getAgentById(Integer id) {
        return agentRepository.findById(id);
    }
    
    public Agent getAgentByEmail(String email) {
        return agentRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Agent not found with email: " + email));
    }
    
    public Agent createAgent(Agent agent) {
        if (agentRepository.findByLicenceNumber(agent.getLicenceNumber()).isPresent()) {
            throw new RuntimeException("License number already exists");
        }
        return agentRepository.save(agent);
    }
    
    public Agent updateAgent(Integer id, Agent agent) {
        if (!agentRepository.existsById(id)) {
            throw new RuntimeException("Agent not found with id: " + id);
        }
        agent.setAgentId(id);
        return agentRepository.save(agent);
    }
    
    public void deleteAgent(Integer id) {
        if (!agentRepository.existsById(id)) {
            throw new RuntimeException("Agent not found with id: " + id);
        }
        agentRepository.deleteById(id);
    }
} 