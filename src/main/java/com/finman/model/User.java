package com.finman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finman.model.enums.UserSituation;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    

    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(columnDefinition = "text")
    private String address;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "known_by_whom")
    private String knownByWhom;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "situation", nullable = false)
    private UserSituation situation = UserSituation.ACTIVE;
    
    @Column(name = "deactivated_date")
    private LocalDate deactivatedDate;
    
    @Column(name = "deactivation_reason", columnDefinition = "text")
    private String deactivationReason;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relacionamentos
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Loan> loans = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();
    
    @OneToMany(mappedBy = "openedByUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CashRegisterSession> openedSessions = new ArrayList<>();
    
    @OneToMany(mappedBy = "closedByUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CashRegisterSession> closedSessions = new ArrayList<>();
    
    @OneToMany(mappedBy = "approvedByUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Loan> approvedLoans = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Document> documents = new ArrayList<>();
    
    // Construtores
    public User() {}
    
    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
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
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    

    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getKnownByWhom() {
        return knownByWhom;
    }
    
    public void setKnownByWhom(String knownByWhom) {
        this.knownByWhom = knownByWhom;
    }
    
    public UserSituation getSituation() {
        return situation;
    }
    
    public void setSituation(UserSituation situation) {
        this.situation = situation;
    }
    
    public LocalDate getDeactivatedDate() {
        return deactivatedDate;
    }
    
    public void setDeactivatedDate(LocalDate deactivatedDate) {
        this.deactivatedDate = deactivatedDate;
    }
    
    public String getDeactivationReason() {
        return deactivationReason;
    }
    
    public void setDeactivationReason(String deactivationReason) {
        this.deactivationReason = deactivationReason;
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
    
    public List<Loan> getLoans() {
        return loans;
    }
    
    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }
    
    public List<Transaction> getTransactions() {
        return transactions;
    }
    
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    
    public List<CashRegisterSession> getOpenedSessions() {
        return openedSessions;
    }
    
    public void setOpenedSessions(List<CashRegisterSession> openedSessions) {
        this.openedSessions = openedSessions;
    }
    
    public List<CashRegisterSession> getClosedSessions() {
        return closedSessions;
    }
    
    public void setClosedSessions(List<CashRegisterSession> closedSessions) {
        this.closedSessions = closedSessions;
    }
    
    public List<Loan> getApprovedLoans() {
        return approvedLoans;
    }
    
    public void setApprovedLoans(List<Loan> approvedLoans) {
        this.approvedLoans = approvedLoans;
    }
    
    public List<Document> getDocuments() {
        return documents;
    }
    
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
    
    // Métodos auxiliares
    public String getFullName() {
        return firstName + " " + lastName;
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
