package com.finman.controller;

import com.finman.model.Loan;
import com.finman.model.User;
import com.finman.model.enums.LoanStatus;
import com.finman.repository.LoanRepository;
import com.finman.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "*")
public class LoanController {
    
    @Autowired
    private LoanRepository loanRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        List<Loan> loans = loanRepository.findAll();
        return ResponseEntity.ok(loans);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long id) {
        Optional<Loan> loan = loanRepository.findById(id);
        return loan.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Loan>> getLoansByUser(@PathVariable Long userId) {
        List<Loan> loans = loanRepository.findByUserId(userId);
        return ResponseEntity.ok(loans);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Loan>> getLoansByStatus(@PathVariable LoanStatus status) {
        List<Loan> loans = loanRepository.findByStatus(status);
        return ResponseEntity.ok(loans);
    }
    
    @PostMapping
    public ResponseEntity<Loan> createLoan(@RequestBody Loan loan) {
        // Validar se o usuário existe
        Optional<User> user = userRepository.findById(loan.getUser().getId());
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Configurar dados iniciais
        loan.setStatus(LoanStatus.PENDING);
        loan.setOutstandingBalance(loan.getLoanAmount());
        loan.setTotalPaidAmount(BigDecimal.ZERO);
        
        Loan savedLoan = loanRepository.save(loan);
        return ResponseEntity.ok(savedLoan);
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<Loan> approveLoan(@PathVariable Long id, @RequestParam Long approvedByUserId) {
        Optional<Loan> loanOpt = loanRepository.findById(id);
        Optional<User> approverOpt = userRepository.findById(approvedByUserId);
        
        if (!loanOpt.isPresent() || !approverOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Loan loan = loanOpt.get();
        User approver = approverOpt.get();
        
        loan.approve(approver);
        Loan savedLoan = loanRepository.save(loan);
        
        return ResponseEntity.ok(savedLoan);
    }
    
    @PutMapping("/{id}/disburse")
    public ResponseEntity<Loan> disburseLoan(@PathVariable Long id) {
        Optional<Loan> loanOpt = loanRepository.findById(id);
        
        if (!loanOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Loan loan = loanOpt.get();
        loan.disburse();
        Loan savedLoan = loanRepository.save(loan);
        
        return ResponseEntity.ok(savedLoan);
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Loan> cancelLoan(@PathVariable Long id) {
        Optional<Loan> loanOpt = loanRepository.findById(id);
        
        if (!loanOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Loan loan = loanOpt.get();
        loan.cancel();
        Loan savedLoan = loanRepository.save(loan);
        
        return ResponseEntity.ok(savedLoan);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        Optional<Loan> loan = loanRepository.findById(id);
        if (loan.isPresent()) {
            loanRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
