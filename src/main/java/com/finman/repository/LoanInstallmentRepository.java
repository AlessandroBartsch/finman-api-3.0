package com.finman.repository;

import com.finman.model.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {
    
    List<LoanInstallment> findByLoanId(Long loanId);
    
    List<LoanInstallment> findByLoanIdAndIsPaidFalse(Long loanId);
    
    List<LoanInstallment> findByLoanIdAndIsPaidTrue(Long loanId);
    
    List<LoanInstallment> findByLoanIdAndDueDateBeforeAndIsPaidFalse(Long loanId, LocalDate date);
    
    List<LoanInstallment> findByDueDateBeforeAndIsPaidFalse(LocalDate date);
}
