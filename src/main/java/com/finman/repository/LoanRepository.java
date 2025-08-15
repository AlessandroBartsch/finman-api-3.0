package com.finman.repository;

import com.finman.model.Loan;
import com.finman.model.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    
    List<Loan> findByUser_Id(Long userId);
    
    List<Loan> findByStatus(LoanStatus status);
    
    List<Loan> findByUser_IdAndStatus(Long userId, LoanStatus status);
    
    List<Loan> findByOutstandingBalanceGreaterThan(java.math.BigDecimal amount);
}
