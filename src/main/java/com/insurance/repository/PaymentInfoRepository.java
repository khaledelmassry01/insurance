package com.insurance.repository;

import com.insurance.model.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Integer> {
    // جلب المدفوعات حسب رقم التأمين
    List<PaymentInfo> findByInsurance_InsuranceId(Integer insuranceId);

    // جلب المدفوعات غير المدفوعة بعد
    List<PaymentInfo> findByPaymentDateIsNull();

    // جلب المدفوعات المتأخرة (تاريخ الاستحقاق قبل اليوم) وغير المدفوعة
    List<PaymentInfo> findByDueDateBeforeAndPaymentDateIsNull(LocalDate date);

    // جلب المدفوعات حسب طريقة الدفع
    List<PaymentInfo> findByPaymentMethod(String paymentMethod);

    @Query("SELECT COUNT(pi) FROM PaymentInfo pi WHERE pi.insurance.agent.agentId = :agentId")
    long countByAgentId(@Param("agentId") Integer agentId);

    @Query("SELECT pi FROM PaymentInfo pi WHERE pi.insurance.agent.agentId = :agentId")
    List<PaymentInfo> findByInsurance_Agent_AgentId(@Param("agentId") Integer agentId);

    @Query("SELECT pi FROM PaymentInfo pi WHERE pi.insurance.insuranceId = :insuranceId AND pi.insurance.agent.agentId = :agentId")
    List<PaymentInfo> findByInsurance_InsuranceIdAndInsurance_Agent_AgentId(@Param("insuranceId") Integer insuranceId, @Param("agentId") Integer agentId);

    @Query("SELECT p FROM PaymentInfo p WHERE p.insurance.agent.agentId = :agentId")
    List<PaymentInfo> findByAgentId(@Param("agentId") Integer agentId);
    
    /**
     * Find payments by policyholder ID
     */
    @Query("SELECT p FROM PaymentInfo p JOIN FETCH p.insurance ic JOIN FETCH ic.policy pol JOIN ic.policyholder ph WHERE ph.policyholderId = :policyholderId")
    List<PaymentInfo> findByInsurancePolicyPolicyholderId(@Param("policyholderId") Integer policyholderId);
    
    /**
     * Check if a payment belongs to a policyholder
     */
    @Query("SELECT COUNT(p) > 0 FROM PaymentInfo p JOIN p.insurance ic JOIN ic.policyholder ph WHERE p.paymentId = :paymentId AND ph.policyholderId = :policyholderId")
    boolean existsByPaymentIdAndInsurancePolicyPolicyholderId(@Param("paymentId") Integer paymentId, @Param("policyholderId") Integer policyholderId);

    @Query(value = """
        SELECT p.payment_id as paymentId,
               p.amount as amount,
               p.payment_date as paymentDate,
               s.status_name as status
        FROM payment_info p
        LEFT JOIN (
            SELECT ph1.payment_id, ph1.status_id
            FROM payment_status_history ph1
            INNER JOIN (
                SELECT payment_id, MAX(status_date) as max_date
                FROM payment_status_history
                GROUP BY payment_id
            ) ph2 ON ph1.payment_id = ph2.payment_id AND ph1.status_date = ph2.max_date
        ) latest ON p.payment_id = latest.payment_id
        LEFT JOIN payment_status s ON latest.status_id = s.status_id
        WHERE p.agent_id = :agentId
        """, nativeQuery = true)
    List<PaymentReportProjection> findAllWithLatestStatusByAgentId(@Param("agentId") Integer agentId);

    @Query(value = """
        SELECT p.payment_id as paymentId,
               p.amount as amount,
               p.payment_date as paymentDate,
               s.status_name as status
        FROM payment_info p
        LEFT JOIN (
            SELECT ph1.payment_id, ph1.status_id
            FROM payment_status_history ph1
            INNER JOIN (
                SELECT payment_id, MAX(status_date) as max_date
                FROM payment_status_history
                GROUP BY payment_id
            ) ph2 ON ph1.payment_id = ph2.payment_id AND ph1.status_date = ph2.max_date
        ) latest ON p.payment_id = latest.payment_id
        LEFT JOIN payment_status s ON latest.status_id = s.status_id
        """, nativeQuery = true)
    List<PaymentReportProjection> findAllWithLatestStatusForAdmin();

    List<PaymentInfo> findAllByPaymentDateBetween(LocalDate start, LocalDate end);
    
    @Query("SELECT p FROM PaymentInfo p WHERE p.insurance.agent.agentId = :agentId AND p.paymentDate BETWEEN :start AND :end")
    List<PaymentInfo> findByAgentIdAndPaymentDateBetween(@Param("agentId") Integer agentId, @Param("start") LocalDate start, @Param("end") LocalDate end);
    
    @Query("SELECT p FROM PaymentInfo p WHERE p.insurance.agent.agentId = :agentId AND p.paymentDate >= :start")
    List<PaymentInfo> findByAgentIdAndPaymentDateAfter(@Param("agentId") Integer agentId, @Param("start") LocalDate start);
    
    @Query("SELECT p FROM PaymentInfo p WHERE p.insurance.agent.agentId = :agentId AND p.paymentDate <= :end")
    List<PaymentInfo> findByAgentIdAndPaymentDateBefore(@Param("agentId") Integer agentId, @Param("end") LocalDate end);
    
    /**
     * Get the latest status for a payment
     * @param paymentId The ID of the payment
     * @return The latest status name (e.g., "Paid", "Pending", etc.)
     */
    @Query(value = """
        SELECT s.status_name
        FROM payment_status_history ph
        JOIN payment_status s ON ph.status_id = s.status_id
        WHERE ph.payment_id = :paymentId
        ORDER BY ph.status_date DESC
        LIMIT 1
        """, nativeQuery = true)
    String findLatestStatusByPaymentId(@Param("paymentId") Integer paymentId);
}
