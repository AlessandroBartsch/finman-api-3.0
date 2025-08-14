package com.finman.controller;

import com.finman.model.Loan;
import com.finman.model.LoanInstallment;
import com.finman.repository.LoanInstallmentRepository;
import com.finman.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/installments")
@CrossOrigin(origins = "*")
public class LoanInstallmentController {
    
    @Autowired
    private LoanInstallmentRepository installmentRepository;
    
    @Autowired
    private LoanRepository loanRepository;
    
    @GetMapping
    public ResponseEntity<List<LoanInstallment>> getAllInstallments() {
        List<LoanInstallment> installments = installmentRepository.findAll();
        return ResponseEntity.ok(installments);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LoanInstallment> getInstallmentById(@PathVariable Long id) {
        Optional<LoanInstallment> installment = installmentRepository.findById(id);
        return installment.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<LoanInstallment>> getInstallmentsByLoan(@PathVariable Long loanId) {
        List<LoanInstallment> installments = installmentRepository.findByLoanId(loanId);
        return ResponseEntity.ok(installments);
    }
    
    @GetMapping("/loan/{loanId}/overdue")
    public ResponseEntity<List<LoanInstallment>> getOverdueInstallments(@PathVariable Long loanId) {
        List<LoanInstallment> installments = installmentRepository.findByLoanIdAndDueDateBeforeAndIsPaidFalse(loanId, LocalDate.now());
        return ResponseEntity.ok(installments);
    }
    
    @PostMapping("/loan/{loanId}")
    public ResponseEntity<LoanInstallment> createInstallment(@PathVariable Long loanId, @RequestBody LoanInstallment installment) {
        Optional<Loan> loan = loanRepository.findById(loanId);
        if (!loan.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        installment.setLoan(loan.get());
        installment.setIsPaid(false);
        installment.setPaidAmount(BigDecimal.ZERO);
        
        LoanInstallment savedInstallment = installmentRepository.save(installment);
        return ResponseEntity.ok(savedInstallment);
    }
    
    @PutMapping("/{id}/pay")
    public ResponseEntity<LoanInstallment> payInstallment(@PathVariable Long id, @RequestParam BigDecimal amount) {
        Optional<LoanInstallment> installmentOpt = installmentRepository.findById(id);
        
        if (!installmentOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        LoanInstallment installment = installmentOpt.get();
        installment.addPayment(amount);
        
        LoanInstallment savedInstallment = installmentRepository.save(installment);
        return ResponseEntity.ok(savedInstallment);
    }
    
    @PutMapping("/{id}/mark-as-paid")
    public ResponseEntity<LoanInstallment> markAsPaid(@PathVariable Long id) {
        Optional<LoanInstallment> installmentOpt = installmentRepository.findById(id);
        
        if (!installmentOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        LoanInstallment installment = installmentOpt.get();
        installment.setIsPaid(true);
        installment.setPaidAmount(installment.getAmount());
        installment.setPaidDate(LocalDate.now());
        
        LoanInstallment savedInstallment = installmentRepository.save(installment);
        return ResponseEntity.ok(savedInstallment);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstallment(@PathVariable Long id) {
        Optional<LoanInstallment> installment = installmentRepository.findById(id);
        if (installment.isPresent()) {
            installmentRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
