package com.insurance.repository;

import com.insurance.model.InsuranceHistory;
import com.insurance.model.InsuranceHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceHistoryRepository
        extends JpaRepository<InsuranceHistory, InsuranceHistoryId> {

    @Query("SELECT COUNT(ih) FROM InsuranceHistory ih WHERE ih.insurance.agent.agentId = :agentId")
    long countByAgentId(@Param("agentId") Integer agentId);
}
