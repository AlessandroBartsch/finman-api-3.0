package com.finman.model;

import com.finman.model.enums.CashMovementType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_movements")
public class CashMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cash_register_session_id", nullable = false)
    private CashRegisterSession cashRegisterSession;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CashMovementType type;
    
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;
    
    @Column(columnDefinition = "text")
    private String description;
    
    @Column(name = "movement_time", nullable = false)
    private LocalDateTime movementTime;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Construtores
    public CashMovement() {}
    
    public CashMovement(CashRegisterSession cashRegisterSession, Transaction transaction,
                       CashMovementType type, BigDecimal amount, String description) {
        this.cashRegisterSession = cashRegisterSession;
        this.transaction = transaction;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.movementTime = LocalDateTime.now();
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
    
    public CashRegisterSession getCashRegisterSession() {
        return cashRegisterSession;
    }
    
    public void setCashRegisterSession(CashRegisterSession cashRegisterSession) {
        this.cashRegisterSession = cashRegisterSession;
    }
    
    public Transaction getTransaction() {
        return transaction;
    }
    
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
    
    public CashMovementType getType() {
        return type;
    }
    
    public void setType(CashMovementType type) {
        this.type = type;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getMovementTime() {
        return movementTime;
    }
    
    public void setMovementTime(LocalDateTime movementTime) {
        this.movementTime = movementTime;
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
