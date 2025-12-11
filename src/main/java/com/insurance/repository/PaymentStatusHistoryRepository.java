package com.insurance.repository;

import com.insurance.model.PaymentStatusHistory;
import com.insurance.model.PaymentStatusHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentStatusHistoryRepository
    extends JpaRepository<PaymentStatusHistory, PaymentStatusHistoryId> {

    /** traverse into the paymentInfo association's paymentId */
    List<PaymentStatusHistory> findByPaymentInfo_PaymentIdOrderByStatusDateDesc(Integer paymentId);

    /** all history entries for a given status */
    List<PaymentStatusHistory> findByStatus_StatusId(Integer statusId);

    /** entries between two status dates */
    List<PaymentStatusHistory> findByStatusDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(psh) FROM PaymentStatusHistory psh WHERE psh.paymentInfo.paymentId = :paymentId")
    long countByPaymentInfo_PaymentId(@Param("paymentId") Integer paymentId);
}
