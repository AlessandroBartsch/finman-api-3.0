package com.finman.dto;

import com.finman.model.LoanInstallment;
import java.math.BigDecimal;
import java.util.List;

public class SimulateInstallmentsResponse {
    
    private List<LoanInstallment> installments;
    private BigDecimal totalPrincipal;
    private BigDecimal totalInterest;
    private BigDecimal totalAmount;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private Integer termValue;
    private String paymentFrequency;
    private String paymentType;
    private String startDate;
    private String endDate;
    
    // Construtores
    public SimulateInstallmentsResponse() {}
    
    public SimulateInstallmentsResponse(List<LoanInstallment> installments, BigDecimal loanAmount,
                                      BigDecimal interestRate, Integer termValue, String paymentFrequency,
                                      String paymentType, String startDate, String endDate) {
        this.installments = installments;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.termValue = termValue;
        this.paymentFrequency = paymentFrequency;
        this.paymentType = paymentType;
        this.startDate = startDate;
        this.endDate = endDate;
        
        // Calcular totais
        this.totalPrincipal = installments.stream()
            .map(LoanInstallment::getPrincipalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        this.totalInterest = installments.stream()
            .map(LoanInstallment::getInterestAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        this.totalAmount = installments.stream()
            .map(LoanInstallment::getTotalDueAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Getters e Setters
    public List<LoanInstallment> getInstallments() {
        return installments;
    }
    
    public void setInstallments(List<LoanInstallment> installments) {
        this.installments = installments;
    }
    
    public BigDecimal getTotalPrincipal() {
        return totalPrincipal;
    }
    
    public void setTotalPrincipal(BigDecimal totalPrincipal) {
        this.totalPrincipal = totalPrincipal;
    }
    
    public BigDecimal getTotalInterest() {
        return totalInterest;
    }
    
    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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
    
    public String getPaymentFrequency() {
        return paymentFrequency;
    }
    
    public void setPaymentFrequency(String paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }
    
    public String getPaymentType() {
        return paymentType;
    }
    
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    
    public String getEndDate() {
        return endDate;
    }
    
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
