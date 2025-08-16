package com.finman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finman.model.enums.LoanStatus;
import com.finman.model.enums.PaymentFrequency;
import com.finman.model.enums.PaymentType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
public class Loan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    // Método para retornar o userId no JSON
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }
    
    @Column(name = "loan_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal loanAmount;
    
    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal interestRate;
    
    @Column(name = "term_value", nullable = false)
    private Integer termValue;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_frequency", nullable = false)
    private PaymentFrequency paymentFrequency;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType = PaymentType.FIXED_INSTALLMENTS;
    
    @Column(name = "alternate_days_interval")
    private Integer alternateDaysInterval;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_user_id")
    @JsonIgnore
    private User approvedByUser;
    
    @Column(name = "disbursement_date")
    private LocalDateTime disbursementDate;
    
    @Column(name = "total_paid_amount", precision = 18, scale = 2)
    private BigDecimal totalPaidAmount = BigDecimal.ZERO;
    
    @Column(name = "outstanding_balance", precision = 18, scale = 2)
    private BigDecimal outstandingBalance;
    
    @Column(name = "total_interest", precision = 18, scale = 2)
    private BigDecimal totalInterest = BigDecimal.ZERO;
    
    @Column(name = "total_loan_value", precision = 18, scale = 2)
    private BigDecimal totalLoanValue = BigDecimal.ZERO;
    
    @Column(name = "charge_interest_separately")
    private Boolean chargeInterestSeparately = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relacionamentos
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<LoanInstallment> installments = new ArrayList<>();
    
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();
    
    // Construtores
    public Loan() {}
    
    public Loan(User user, BigDecimal loanAmount, BigDecimal interestRate, 
                Integer termValue, PaymentFrequency paymentFrequency, 
                LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.termValue = termValue;
        this.paymentFrequency = paymentFrequency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = LoanStatus.PENDING;
        this.outstandingBalance = loanAmount;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public LoanStatus getStatus() {
        return status;
    }
    
    public void setStatus(LoanStatus status) {
        this.status = status;
    }
    
    public User getApprovedByUser() {
        return approvedByUser;
    }
    
    public void setApprovedByUser(User approvedByUser) {
        this.approvedByUser = approvedByUser;
    }
    
    public LocalDateTime getDisbursementDate() {
        return disbursementDate;
    }
    
    public void setDisbursementDate(LocalDateTime disbursementDate) {
        this.disbursementDate = disbursementDate;
    }
    
    public BigDecimal getTotalPaidAmount() {
        return totalPaidAmount;
    }
    
    public void setTotalPaidAmount(BigDecimal totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }
    
    public BigDecimal getOutstandingBalance() {
        return outstandingBalance;
    }
    
    public void setOutstandingBalance(BigDecimal outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }
    
    public BigDecimal getTotalInterest() {
        return totalInterest;
    }
    
    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }
    
    public BigDecimal getTotalLoanValue() {
        return totalLoanValue;
    }
    
    public void setTotalLoanValue(BigDecimal totalLoanValue) {
        this.totalLoanValue = totalLoanValue;
    }
    
    public Boolean getChargeInterestSeparately() {
        return chargeInterestSeparately;
    }
    
    public void setChargeInterestSeparately(Boolean chargeInterestSeparately) {
        this.chargeInterestSeparately = chargeInterestSeparately;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<LoanInstallment> getInstallments() {
        return installments;
    }
    
    public void setInstallments(List<LoanInstallment> installments) {
        this.installments = installments;
    }
    
    public List<Transaction> getTransactions() {
        return transactions;
    }
    
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    
    // Métodos auxiliares
    public void approve(User approvedByUser) {
        this.status = LoanStatus.APPROVED;
        this.approvedByUser = approvedByUser;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void activate() {
        this.status = LoanStatus.ACTIVE;
        this.disbursementDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void disburse() {
        this.status = LoanStatus.ACTIVE;
        this.disbursementDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancel() {
        this.status = LoanStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void revertToPending() {
        this.status = LoanStatus.PENDING;
        this.approvedByUser = null;
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
