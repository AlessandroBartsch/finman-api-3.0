package com.finman.model;

import com.finman.model.enums.CashRegisterSessionStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cash_register_sessions")
public class CashRegisterSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cash_register_id", nullable = false)
    private CashRegister cashRegister;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opened_by_user_id", nullable = false)
    private User openedByUser;
    
    @Column(name = "opening_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal openingBalance;
    
    @Column(name = "opening_time", nullable = false)
    private LocalDateTime openingTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by_user_id")
    private User closedByUser;
    
    @Column(name = "closing_balance", precision = 18, scale = 2)
    private BigDecimal closingBalance;
    
    @Column(name = "closing_time")
    private LocalDateTime closingTime;
    
    @Column(name = "expected_closing_balance", precision = 18, scale = 2)
    private BigDecimal expectedClosingBalance;
    
    @Column(name = "discrepancy_amount", precision = 18, scale = 2)
    private BigDecimal discrepancyAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CashRegisterSessionStatus status;
    
    @Column(columnDefinition = "text")
    private String note;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relacionamentos
    @OneToMany(mappedBy = "cashRegisterSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CashMovement> movements = new ArrayList<>();
    
    // Construtores
    public CashRegisterSession() {}
    
    public CashRegisterSession(CashRegister cashRegister, User openedByUser, BigDecimal openingBalance) {
        this.cashRegister = cashRegister;
        this.openedByUser = openedByUser;
        this.openingBalance = openingBalance;
        this.openingTime = LocalDateTime.now();
        this.status = CashRegisterSessionStatus.OPEN;
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
    
    public CashRegister getCashRegister() {
        return cashRegister;
    }
    
    public void setCashRegister(CashRegister cashRegister) {
        this.cashRegister = cashRegister;
    }
    
    public User getOpenedByUser() {
        return openedByUser;
    }
    
    public void setOpenedByUser(User openedByUser) {
        this.openedByUser = openedByUser;
    }
    
    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }
    
    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }
    
    public LocalDateTime getOpeningTime() {
        return openingTime;
    }
    
    public void setOpeningTime(LocalDateTime openingTime) {
        this.openingTime = openingTime;
    }
    
    public User getClosedByUser() {
        return closedByUser;
    }
    
    public void setClosedByUser(User closedByUser) {
        this.closedByUser = closedByUser;
    }
    
    public BigDecimal getClosingBalance() {
        return closingBalance;
    }
    
    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }
    
    public LocalDateTime getClosingTime() {
        return closingTime;
    }
    
    public void setClosingTime(LocalDateTime closingTime) {
        this.closingTime = closingTime;
    }
    
    public BigDecimal getExpectedClosingBalance() {
        return expectedClosingBalance;
    }
    
    public void setExpectedClosingBalance(BigDecimal expectedClosingBalance) {
        this.expectedClosingBalance = expectedClosingBalance;
    }
    
    public BigDecimal getDiscrepancyAmount() {
        return discrepancyAmount;
    }
    
    public void setDiscrepancyAmount(BigDecimal discrepancyAmount) {
        this.discrepancyAmount = discrepancyAmount;
    }
    
    public CashRegisterSessionStatus getStatus() {
        return status;
    }
    
    public void setStatus(CashRegisterSessionStatus status) {
        this.status = status;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
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
    
    public List<CashMovement> getMovements() {
        return movements;
    }
    
    public void setMovements(List<CashMovement> movements) {
        this.movements = movements;
    }
    
    // Métodos auxiliares
    public void close(User closedByUser, BigDecimal closingBalance, String note) {
        this.closedByUser = closedByUser;
        this.closingBalance = closingBalance;
        this.closingTime = LocalDateTime.now();
        this.status = CashRegisterSessionStatus.CLOSED;
        this.note = note;
        
        // Calcular discrepância
        if (this.expectedClosingBalance != null) {
            this.discrepancyAmount = closingBalance.subtract(expectedClosingBalance);
        }
        
        this.updatedAt = LocalDateTime.now();
    }
    
    public BigDecimal getCurrentBalance() {
        if (status == CashRegisterSessionStatus.CLOSED) {
            return closingBalance;
        }
        
        BigDecimal totalInflow = movements.stream()
            .filter(m -> m.getType() == com.finman.model.enums.CashMovementType.INFLOW)
            .map(m -> m.getAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal totalOutflow = movements.stream()
            .filter(m -> m.getType() == com.finman.model.enums.CashMovementType.OUTFLOW)
            .map(m -> m.getAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        return openingBalance.add(totalInflow).subtract(totalOutflow);
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
