package com.insurance.repository;

import com.insurance.model.ClaimStatusDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimStatusDetailsRepository extends JpaRepository<ClaimStatusDetails, Integer> {
    /**
     * Find a claim status details by status name
     * @param statusName The name of the status
     * @return The claim status details if found
     */
    @Query("SELECT s FROM ClaimStatusDetails s WHERE s.statusName = :statusName")
    Optional<ClaimStatusDetails> findByStatusName(@Param("statusName") String statusName);
}