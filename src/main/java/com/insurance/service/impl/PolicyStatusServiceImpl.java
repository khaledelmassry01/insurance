package com.insurance.service.impl;

import com.insurance.model.PolicyStatus;
import com.insurance.repository.PolicyStatusRepository;
import com.insurance.service.PolicyStatusService;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the PolicyStatusService interface.
 */
@Service
public class PolicyStatusServiceImpl implements PolicyStatusService {

    private final PolicyStatusRepository policyStatusRepository;

    public PolicyStatusServiceImpl(PolicyStatusRepository policyStatusRepository) {
        this.policyStatusRepository = policyStatusRepository;
    }

    /**
     * Retrieves all policy statuses from the repository.
     *
     * @return A list of all PolicyStatus entities.
     */
    @Override
    public List<PolicyStatus> getAllStatuses() {
        // Assuming PolicyStatusRepository extends JpaRepository or similar,
        // findAll() will retrieve all records.
        return policyStatusRepository.findAll();
    }
}
