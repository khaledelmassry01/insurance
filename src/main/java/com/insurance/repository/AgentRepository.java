package com.insurance.repository;

import com.insurance.model.Agent;
import com.insurance.model.InsuranceCoverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Integer> {

    Optional<Agent> findByLicenceNumber(String licenceNumber);

    @NonNull
    Optional<Agent> findById(@NonNull Integer id);

    // Renamed from findByUsername to findByEmail
    Optional<Agent> findByEmail(String email); // ✅ هذا السطر هو الذي يستخدم لتسجيل الدخول

    @Query("SELECT ic FROM InsuranceCoverage ic WHERE ic.agent.agentId = :agentId")
    List<InsuranceCoverage> findPoliciesByAgentId(@Param("agentId") Integer agentId);

    @Query("SELECT COUNT(ic) FROM InsuranceCoverage ic WHERE ic.agent.agentId = :agentId AND ic.status.statusName = 'ACTIVE'")
    Long countActivePoliciesByAgentId(@Param("agentId") Integer agentId);

    @Query("SELECT COALESCE(SUM(ic.premium), 0) FROM InsuranceCoverage ic WHERE ic.agent.agentId = :agentId")
    BigDecimal calculateTotalPremiumByAgentId(@Param("agentId") Integer agentId);
}
