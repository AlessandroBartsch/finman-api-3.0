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
    
    // Campos para cálculo de atraso
    @Column(name = "daily_interest_rate", precision = 5, scale = 2)
    private BigDecimal dailyInterestRate = new BigDecimal("1.00"); // Padrão 1% ao dia
    
    @Column(name = "overdue_days")
    private Integer overdueDays = 0;
    
    @Column(name = "daily_interest_amount", precision = 18, scale = 2)
    private BigDecimal dailyInterestAmount = BigDecimal.ZERO;
    
    @Column(name = "overdue_interest_amount", precision = 18, scale = 2)
    private BigDecimal overdueInterestAmount = BigDecimal.ZERO;
    
    @Column(name = "total_with_overdue", precision = 18, scale = 2)
    private BigDecimal totalWithOverdue = BigDecimal.ZERO;
    
    @Column(name = "negotiation_comment", columnDefinition = "text")
    private String negotiationComment;
    
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
        this.totalWithOverdue = this.totalDueAmount; // Inicialmente igual ao valor original
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
    
    // Getters e Setters para campos de atraso
    public BigDecimal getDailyInterestRate() {
        return dailyInterestRate;
    }
    
    public void setDailyInterestRate(BigDecimal dailyInterestRate) {
        this.dailyInterestRate = dailyInterestRate;
    }
    
    public Integer getOverdueDays() {
        return overdueDays;
    }
    
    public void setOverdueDays(Integer overdueDays) {
        this.overdueDays = overdueDays;
    }
    
    public BigDecimal getDailyInterestAmount() {
        return dailyInterestAmount;
    }
    
    public void setDailyInterestAmount(BigDecimal dailyInterestAmount) {
        this.dailyInterestAmount = dailyInterestAmount;
    }
    
    public BigDecimal getOverdueInterestAmount() {
        return overdueInterestAmount;
    }
    
    public void setOverdueInterestAmount(BigDecimal overdueInterestAmount) {
        this.overdueInterestAmount = overdueInterestAmount;
    }
    
    public BigDecimal getTotalWithOverdue() {
        return totalWithOverdue;
    }
    
    public void setTotalWithOverdue(BigDecimal totalWithOverdue) {
        this.totalWithOverdue = totalWithOverdue;
    }
    
    public String getNegotiationComment() {
        return negotiationComment;
    }
    
    public void setNegotiationComment(String negotiationComment) {
        this.negotiationComment = negotiationComment;
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
    
    /**
     * Calcula os juros de atraso baseado na planilha do usuário
     * Fórmula: VALOR P/DIA = VALOR × (JUROS AO DIA / 100)
     * JUROS DO PERÍODO = VALOR P/DIA × QUANTIDADE DE DIAS EM ATRASO
     * JUROS MAIS CAPITAL = VALOR + JUROS DO PERÍODO
     */
    public void calculateOverdueInterest() {
        // Se a parcela está paga, o totalWithOverdue deve ser igual ao valor pago
        if (this.isPaid) {
            this.overdueDays = 0;
            this.dailyInterestAmount = BigDecimal.ZERO;
            this.overdueInterestAmount = BigDecimal.ZERO;
            this.totalWithOverdue = this.paidAmount;
            return;
        }
        
        if (!isOverdue()) {
            this.overdueDays = 0;
            this.dailyInterestAmount = BigDecimal.ZERO;
            this.overdueInterestAmount = BigDecimal.ZERO;
            this.totalWithOverdue = this.totalDueAmount;
            return;
        }
        
        // Calcular dias em atraso
        this.overdueDays = (int) java.time.temporal.ChronoUnit.DAYS.between(this.dueDate, LocalDate.now());
        
        // VALOR P/DIA = VALOR × (JUROS AO DIA / 100)
        BigDecimal dailyRateDecimal = this.dailyInterestRate.divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP);
        this.dailyInterestAmount = this.totalDueAmount.multiply(dailyRateDecimal).setScale(2, java.math.RoundingMode.HALF_UP);
        
        // JUROS DO PERÍODO = VALOR P/DIA × QUANTIDADE DE DIAS EM ATRASO
        this.overdueInterestAmount = this.dailyInterestAmount.multiply(BigDecimal.valueOf(this.overdueDays)).setScale(2, java.math.RoundingMode.HALF_UP);
        
        // JUROS MAIS CAPITAL = VALOR + JUROS DO PERÍODO
        this.totalWithOverdue = this.totalDueAmount.add(this.overdueInterestAmount);
    }
    
    /**
     * Calcula juros de atraso até uma data específica
     */
    public void calculateOverdueInterestUntil(LocalDate calculateUntilDate) {
        // Se a parcela está paga, o totalWithOverdue deve ser igual ao valor pago
        if (this.isPaid) {
            this.overdueDays = 0;
            this.dailyInterestAmount = BigDecimal.ZERO;
            this.overdueInterestAmount = BigDecimal.ZERO;
            this.totalWithOverdue = this.paidAmount;
            return;
        }
        
        if (this.dueDate.isAfter(calculateUntilDate)) {
            this.overdueDays = 0;
            this.dailyInterestAmount = BigDecimal.ZERO;
            this.overdueInterestAmount = BigDecimal.ZERO;
            this.totalWithOverdue = this.totalDueAmount;
            return;
        }
        
        // Calcular dias em atraso até a data especificada
        this.overdueDays = (int) java.time.temporal.ChronoUnit.DAYS.between(this.dueDate, calculateUntilDate);
        
        if (this.overdueDays <= 0) {
            this.dailyInterestAmount = BigDecimal.ZERO;
            this.overdueInterestAmount = BigDecimal.ZERO;
            this.totalWithOverdue = this.totalDueAmount;
            return;
        }
        
        // VALOR P/DIA = VALOR × (JUROS AO DIA / 100)
        BigDecimal dailyRateDecimal = this.dailyInterestRate.divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP);
        this.dailyInterestAmount = this.totalDueAmount.multiply(dailyRateDecimal).setScale(2, java.math.RoundingMode.HALF_UP);
        
        // JUROS DO PERÍODO = VALOR P/DIA × QUANTIDADE DE DIAS EM ATRASO
        this.overdueInterestAmount = this.dailyInterestAmount.multiply(BigDecimal.valueOf(this.overdueDays)).setScale(2, java.math.RoundingMode.HALF_UP);
        
        // JUROS MAIS CAPITAL = VALOR + JUROS DO PERÍODO
        this.totalWithOverdue = this.totalDueAmount.add(this.overdueInterestAmount);
    }
    
    public void addPayment(BigDecimal amount) {
        this.paidAmount = this.paidAmount.add(amount);
        if (this.paidAmount.compareTo(this.totalDueAmount) >= 0) {
            this.isPaid = true;
            this.paidAt = LocalDateTime.now();
            // Quando a parcela é paga, o totalWithOverdue deve ser igual ao valor pago
            this.totalWithOverdue = this.paidAmount;
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
