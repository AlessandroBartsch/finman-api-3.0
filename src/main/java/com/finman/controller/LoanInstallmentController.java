package com.finman.controller;

import com.finman.model.Loan;
import com.finman.model.LoanInstallment;
import com.finman.model.enums.LoanStatus;
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
    
    @GetMapping("/loan/{loanId}/with-overdue-calculation")
    public ResponseEntity<List<LoanInstallment>> getInstallmentsWithOverdueCalculation(@PathVariable Long loanId) {
        List<LoanInstallment> installments = installmentRepository.findByLoanId(loanId);
        
        // Calcular juros de atraso para cada parcela
        for (LoanInstallment installment : installments) {
            installment.calculateOverdueInterest();
        }
        
        return ResponseEntity.ok(installments);
    }
    
    @GetMapping("/loan/{loanId}/with-overdue-calculation-until")
    public ResponseEntity<List<LoanInstallment>> getInstallmentsWithOverdueCalculationUntil(
            @PathVariable Long loanId, 
            @RequestParam String calculateUntilDate) {
        
        List<LoanInstallment> installments = installmentRepository.findByLoanId(loanId);
        LocalDate untilDate = LocalDate.parse(calculateUntilDate);
        
        // Calcular juros de atraso para cada parcela até a data especificada
        for (LoanInstallment installment : installments) {
            installment.calculateOverdueInterestUntil(untilDate);
        }
        
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
    public ResponseEntity<LoanInstallment> payInstallment(@PathVariable Long id, @RequestParam BigDecimal amount, 
                                                         @RequestParam(required = false) String comment) {
        Optional<LoanInstallment> installmentOpt = installmentRepository.findById(id);
        
        if (!installmentOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        LoanInstallment installment = installmentOpt.get();
        
        // Verificar se o empréstimo está ativo
        if (!installment.getLoan().getStatus().equals(LoanStatus.ACTIVE)) {
            return ResponseEntity.badRequest().body(null);
        }
        
        // Salvar comentário se fornecido
        if (comment != null && !comment.trim().isEmpty()) {
            installment.setNegotiationComment(comment.trim());
        }
        
        installment.addPayment(amount);
        
        // Recalcular totalWithOverdue para refletir o valor pago
        installment.calculateOverdueInterest();
        
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
        
        // Verificar se o empréstimo está ativo
        if (!installment.getLoan().getStatus().equals(LoanStatus.ACTIVE)) {
            return ResponseEntity.badRequest().body(null);
        }
        
        installment.setIsPaid(true);
        installment.setPaidAmount(installment.getAmount());
        installment.setPaidDate(LocalDate.now());
        
        // Recalcular totalWithOverdue para refletir o valor pago
        installment.calculateOverdueInterest();
        
        LoanInstallment savedInstallment = installmentRepository.save(installment);
        return ResponseEntity.ok(savedInstallment);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LoanInstallment> updateInstallment(@PathVariable Long id, @RequestBody LoanInstallment installmentUpdate) {
        Optional<LoanInstallment> installmentOpt = installmentRepository.findById(id);
        
        if (!installmentOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        LoanInstallment installment = installmentOpt.get();
        
        // Atualizar campos permitidos
        if (installmentUpdate.getInstallmentNumber() != null) {
            installment.setInstallmentNumber(installmentUpdate.getInstallmentNumber());
        }
        if (installmentUpdate.getDueDate() != null) {
            installment.setDueDate(installmentUpdate.getDueDate());
        }
        if (installmentUpdate.getPrincipalAmount() != null) {
            installment.setPrincipalAmount(installmentUpdate.getPrincipalAmount());
        }
        if (installmentUpdate.getInterestAmount() != null) {
            installment.setInterestAmount(installmentUpdate.getInterestAmount());
        }
        if (installmentUpdate.getDailyInterestRate() != null) {
            installment.setDailyInterestRate(installmentUpdate.getDailyInterestRate());
        }
        
        // Recalcular valores derivados
        installment.setTotalDueAmount(installment.getPrincipalAmount().add(installment.getInterestAmount()));
        
        // Recalcular juros de atraso
        installment.calculateOverdueInterest();
        
        LoanInstallment savedInstallment = installmentRepository.save(installment);
        return ResponseEntity.ok(savedInstallment);
    }
    
    @PutMapping("/{id}/update-daily-interest-rate")
    public ResponseEntity<LoanInstallment> updateDailyInterestRate(
            @PathVariable Long id, 
            @RequestParam BigDecimal dailyInterestRate) {
        
        Optional<LoanInstallment> installmentOpt = installmentRepository.findById(id);
        
        if (!installmentOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        LoanInstallment installment = installmentOpt.get();
        
        // Verificar se o empréstimo está ativo
        if (!installment.getLoan().getStatus().equals(LoanStatus.ACTIVE)) {
            return ResponseEntity.badRequest().body(null);
        }
        
        installment.setDailyInterestRate(dailyInterestRate);
        
        // Recalcular juros de atraso
        installment.calculateOverdueInterest();
        
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
