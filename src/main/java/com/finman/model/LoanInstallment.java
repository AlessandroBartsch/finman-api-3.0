package com.finman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan_installments")
public class LoanInstallment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    @JsonIgnore
    private Loan loan;
    
    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Column(name = "principal_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal principalAmount;
    
    @Column(name = "interest_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal interestAmount;
    
    @Column(name = "total_due_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalDueAmount;
    
    @Column(name = "paid_amount", precision = 18, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;
    
    @Column(name = "is_paid")
    private Boolean isPaid = false;
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relacionamentos
    @OneToMany(mappedBy = "installment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();
    
    // Construtores
    public LoanInstallment() {}
    
    public LoanInstallment(Loan loan, Integer installmentNumber, LocalDate dueDate,
                          BigDecimal principalAmount, BigDecimal interestAmount) {
        this.loan = loan;
        this.installmentNumber = installmentNumber;
        this.dueDate = dueDate;
        this.principalAmount = principalAmount;
        this.interestAmount = interestAmount;
        this.totalDueAmount = principalAmount.add(interestAmount);
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
    
    public Loan getLoan() {
        return loan;
    }
    
    public void setLoan(Loan loan) {
        this.loan = loan;
    }
    
    public Integer getInstallmentNumber() {
        return installmentNumber;
    }
    
    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }
    
    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }
    
    public BigDecimal getInterestAmount() {
        return interestAmount;
    }
    
    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }
    
    public BigDecimal getTotalDueAmount() {
        return totalDueAmount;
    }
    
    public void setTotalDueAmount(BigDecimal totalDueAmount) {
        this.totalDueAmount = totalDueAmount;
    }
    
    public BigDecimal getPaidAmount() {
        return paidAmount;
    }
    
    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }
    
    public Boolean getIsPaid() {
        return isPaid;
    }
    
    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }
    
    public LocalDateTime getPaidAt() {
        return paidAt;
    }
    
    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
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
    
    public List<Transaction> getTransactions() {
        return transactions;
    }
    
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    
    // Métodos auxiliares
    public BigDecimal getRemainingAmount() {
        return totalDueAmount.subtract(paidAmount);
    }
    
    public boolean isOverdue() {
        return !isPaid && dueDate.isBefore(LocalDate.now());
    }
    
    public void addPayment(BigDecimal amount) {
        this.paidAmount = this.paidAmount.add(amount);
        if (this.paidAmount.compareTo(this.totalDueAmount) >= 0) {
            this.isPaid = true;
            this.paidAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    // Método para compatibilidade com o controller
    public BigDecimal getAmount() {
        return this.totalDueAmount;
    }
    
    public void setPaidDate(LocalDate paidDate) {
        // Convertendo LocalDate para LocalDateTime
        this.paidAt = paidDate.atStartOfDay();
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
