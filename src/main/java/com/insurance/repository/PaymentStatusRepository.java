package com.insurance.repository;

import com.insurance.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Integer> {

    // existing lookups
    Optional<PaymentStatus> findByStatusName(String statusName);
    boolean existsByStatusName(String statusName);

    // case-insensitive variants, if you ever need them
    Optional<PaymentStatus> findByStatusNameIgnoreCase(String statusName);
    boolean existsByStatusNameIgnoreCase(String statusName);

    // and perhaps to drive dropdowns in your UI:
    List<PaymentStatus> findAllByOrderByStatusNameAsc();
}
