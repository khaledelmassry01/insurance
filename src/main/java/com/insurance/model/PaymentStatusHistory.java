package com.insurance.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "payment_status_history")
@IdClass(PaymentStatusHistoryId.class)
public class PaymentStatusHistory {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentInfo paymentInfo;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private PaymentStatus status;

    @Id
    @Column(name = "status_date", nullable = false)
    private LocalDate statusDate;

    public PaymentStatusHistory() {}

    public PaymentStatusHistory(PaymentInfo paymentInfo, PaymentStatus status, LocalDate statusDate) {
        this.paymentInfo = paymentInfo;
        this.status = status;
        this.statusDate = statusDate;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDate getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(LocalDate statusDate) {
        this.statusDate = statusDate;
    }
}
