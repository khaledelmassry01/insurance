package com.insurance.service;

import com.insurance.model.PolicyStatus;

import java.util.List;

/**
 * Service interface for managing Policy Statuses.
 */
public interface PolicyStatusService {

    /**
     * Retrieves all policy statuses.
     *
     * @return A list of all PolicyStatus entities.
     */
    List<PolicyStatus> getAllStatuses();
}
