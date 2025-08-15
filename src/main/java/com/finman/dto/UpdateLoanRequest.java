package com.finman.dto;

import com.finman.model.enums.LoanStatus;
import com.finman.model.enums.PaymentFrequency;
import com.finman.model.enums.PaymentType;
import java.math.BigDecimal;
import java.time.LocalDate;

public class UpdateLoanRequest {
    private Long userId;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private Integer termValue;
    private PaymentFrequency paymentFrequency;
    private PaymentType paymentType;
    private Integer alternateDaysInterval;
    private LocalDate startDate;
    private LoanStatus status;

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTermValue() {
        return termValue;
    }

    public void setTermValue(Integer termValue) {
        this.termValue = termValue;
    }

    public PaymentFrequency getPaymentFrequency() {
        return paymentFrequency;
    }

    public void setPaymentFrequency(PaymentFrequency paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Integer getAlternateDaysInterval() {
        return alternateDaysInterval;
    }

    public void setAlternateDaysInterval(Integer alternateDaysInterval) {
        this.alternateDaysInterval = alternateDaysInterval;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }
}
