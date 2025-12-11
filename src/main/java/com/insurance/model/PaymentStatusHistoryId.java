package com.insurance.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Composite key class for PaymentStatusHistory.
 */
public class PaymentStatusHistoryId implements Serializable {
    private Integer paymentInfo;
    private Integer status;
    private LocalDate statusDate;

    public PaymentStatusHistoryId() {}

    public PaymentStatusHistoryId(Integer paymentInfo, Integer status, LocalDate statusDate) {
        this.paymentInfo = paymentInfo;
        this.status = status;
        this.statusDate = statusDate;
    }

    public Integer getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(Integer paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDate getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(LocalDate statusDate) {
        this.statusDate = statusDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentStatusHistoryId)) return false;
        PaymentStatusHistoryId that = (PaymentStatusHistoryId) o;
        return Objects.equals(paymentInfo, that.paymentInfo) &&
               Objects.equals(status, that.status) &&
               Objects.equals(statusDate, that.statusDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentInfo, status, statusDate);
    }
}
