package com.insurance.repository;

import com.insurance.model.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolicyStatusRepository extends JpaRepository<PolicyStatus, Integer> {
    Optional<PolicyStatus> findByStatusName(String statusName);
} 