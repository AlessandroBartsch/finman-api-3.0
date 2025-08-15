package com.finman.controller;

import com.finman.model.Loan;
import com.finman.model.User;
import com.finman.model.LoanInstallment;
import com.finman.model.enums.LoanStatus;
import com.finman.model.enums.PaymentFrequency;
import com.finman.model.enums.PaymentType;
import com.finman.repository.LoanRepository;
import com.finman.repository.UserRepository;
import com.finman.dto.CreateLoanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.math.RoundingMode;

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
        List<Loan> loans = loanRepository.findByUser_Id(userId);
        return ResponseEntity.ok(loans);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Loan>> getLoansByStatus(@PathVariable LoanStatus status) {
        List<Loan> loans = loanRepository.findByStatus(status);
        return ResponseEntity.ok(loans);
    }
    
    @PostMapping
    public ResponseEntity<Loan> createLoan(@RequestBody CreateLoanRequest request) {
        // Validar se o usuário existe
        Optional<User> user = userRepository.findById(request.getUserId());
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }
        
        // Criar o empréstimo
        Loan loan = new Loan();
        loan.setUser(user.get());
        loan.setLoanAmount(request.getLoanAmount());
        loan.setInterestRate(request.getInterestRate());
        loan.setTermValue(request.getTermValue());
        loan.setPaymentFrequency(request.getPaymentFrequency());
        loan.setPaymentType(request.getPaymentType() != null ? request.getPaymentType() : PaymentType.FIXED_INSTALLMENTS);
        loan.setAlternateDaysInterval(request.getAlternateDaysInterval());
        loan.setStartDate(request.getStartDate());
        
        // Calcular a data de fim baseada na frequência de pagamento
        LocalDate endDate = calculateEndDate(request.getStartDate(), request.getTermValue(), request.getPaymentFrequency());
        loan.setEndDate(endDate);
        
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
    
    @PutMapping("/{id}")
    public ResponseEntity<Loan> updateLoan(@PathVariable Long id, @RequestBody CreateLoanRequest request) {
        Optional<Loan> loanOpt = loanRepository.findById(id);
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        
        if (!loanOpt.isPresent() || !userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Loan loan = loanOpt.get();
        User user = userOpt.get();
        
        // Atualizar campos básicos
        loan.setUser(user);
        loan.setLoanAmount(request.getLoanAmount());
        loan.setInterestRate(request.getInterestRate());
        loan.setTermValue(request.getTermValue());
        loan.setPaymentFrequency(request.getPaymentFrequency());
        loan.setPaymentType(request.getPaymentType() != null ? request.getPaymentType() : PaymentType.FIXED_INSTALLMENTS);
        loan.setAlternateDaysInterval(request.getAlternateDaysInterval());
        loan.setStartDate(request.getStartDate());
        
        // Recalcular data de fim
        LocalDate endDate = calculateEndDate(request.getStartDate(), request.getTermValue(), request.getPaymentFrequency());
        loan.setEndDate(endDate);
        
        // Recalcular saldo devedor se necessário
        if (loan.getStatus() == LoanStatus.PENDING) {
            loan.setOutstandingBalance(loan.getLoanAmount());
        }
        
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
    
    @GetMapping("/{id}/simulate-installments")
    public ResponseEntity<List<LoanInstallment>> simulateInstallments(@PathVariable Long id) {
        Optional<Loan> loanOpt = loanRepository.findById(id);
        if (!loanOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Loan loan = loanOpt.get();
        List<LoanInstallment> simulatedInstallments = generateSimulatedInstallments(loan);
        return ResponseEntity.ok(simulatedInstallments);
    }
    
    private LocalDate calculateEndDate(LocalDate startDate, Integer termValue, PaymentFrequency frequency) {
        LocalDate endDate = startDate;
        
        switch (frequency) {
            case DAILY:
                endDate = startDate.plusDays(termValue - 1);
                break;
            case WEEKLY:
                endDate = startDate.plusWeeks(termValue - 1);
                break;
            case MONTHLY:
                endDate = startDate.plusMonths(termValue - 1);
                break;
            case ALTERNATE_DAYS:
                // Para dias alternados, assumimos que termValue é o número de parcelas
                // e cada parcela é a cada 2 dias
                endDate = startDate.plusDays((termValue - 1) * 2);
                break;
        }
        
        return endDate;
    }
    
    private List<LoanInstallment> generateSimulatedInstallments(Loan loan) {
        List<LoanInstallment> installments = new ArrayList<>();
        BigDecimal remainingPrincipal = loan.getLoanAmount();
        LocalDate currentDate = loan.getStartDate();
        
        switch (loan.getPaymentType()) {
            case FIXED_INSTALLMENTS:
                return generateFixedInstallments(loan, remainingPrincipal, currentDate);
            case INTEREST_ONLY:
                return generateInterestOnlyInstallments(loan, remainingPrincipal, currentDate);
            case FLEXIBLE:
                return generateFlexibleInstallments(loan, remainingPrincipal, currentDate);
            default:
                return generateFixedInstallments(loan, remainingPrincipal, currentDate);
        }
    }
    
    private List<LoanInstallment> generateFixedInstallments(Loan loan, BigDecimal remainingPrincipal, LocalDate currentDate) {
        List<LoanInstallment> installments = new ArrayList<>();
        
        // Cálculo de juros compostos conforme especificação
        // Exemplo: R$ 1.000,00 a 20% a.m. em 3 meses
        // 1º mês: 1000 + 20% = 1200, parcela = 400 + 200 = 600
        // 2º mês: 800 + 20% = 960, parcela = 400 + 160 = 560  
        // 3º mês: 400 + 20% = 480, parcela = 400 + 80 = 480
        
        BigDecimal totalPrincipal = loan.getLoanAmount();
        BigDecimal monthlyInterestRate = loan.getInterestRate(); // Já é mensal (ex: 0.20 para 20%)
        
        for (int i = 1; i <= loan.getTermValue(); i++) {
            // Calcular juros sobre o saldo devedor atual
            BigDecimal interestAmount = remainingPrincipal.multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP);
            
            // Calcular valor da parcela (principal + juros)
            BigDecimal principalPerInstallment = totalPrincipal.divide(BigDecimal.valueOf(loan.getTermValue()), 2, RoundingMode.HALF_UP);
            
            // Para a última parcela, ajustar o principal para não ter diferença
            BigDecimal currentPrincipalAmount = principalPerInstallment;
            if (i == loan.getTermValue()) {
                currentPrincipalAmount = remainingPrincipal;
            }
            
            BigDecimal totalDueAmount = currentPrincipalAmount.add(interestAmount);
            
            LoanInstallment installment = createInstallment(loan, i, currentDate, currentPrincipalAmount, interestAmount, totalDueAmount);
            installments.add(installment);
            
            // Atualizar saldo devedor e próxima data
            remainingPrincipal = remainingPrincipal.subtract(currentPrincipalAmount);
            currentDate = calculateNextDate(currentDate, loan.getPaymentFrequency(), loan.getAlternateDaysInterval());
        }
        
        return installments;
    }
    
    private List<LoanInstallment> generateInterestOnlyInstallments(Loan loan, BigDecimal remainingPrincipal, LocalDate currentDate) {
        List<LoanInstallment> installments = new ArrayList<>();
        BigDecimal monthlyInterestRate = loan.getInterestRate(); // Já é mensal (ex: 0.20 para 20%)
        
        for (int i = 1; i <= loan.getTermValue(); i++) {
            // Calcular juros sobre o saldo devedor atual
            BigDecimal interestAmount = remainingPrincipal.multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP);
            
            // Para as parcelas normais, só paga juros
            BigDecimal currentPrincipalAmount = BigDecimal.ZERO;
            if (i == loan.getTermValue()) {
                // Na última parcela, paga o principal + juros
                currentPrincipalAmount = remainingPrincipal;
            }
            
            BigDecimal totalDueAmount = currentPrincipalAmount.add(interestAmount);
            
            LoanInstallment installment = createInstallment(loan, i, currentDate, currentPrincipalAmount, interestAmount, totalDueAmount);
            installments.add(installment);
            
            // Atualizar saldo devedor e próxima data
            remainingPrincipal = remainingPrincipal.subtract(currentPrincipalAmount);
            currentDate = calculateNextDate(currentDate, loan.getPaymentFrequency(), loan.getAlternateDaysInterval());
        }
        
        return installments;
    }
    
    private List<LoanInstallment> generateFlexibleInstallments(Loan loan, BigDecimal remainingPrincipal, LocalDate currentDate) {
        List<LoanInstallment> installments = new ArrayList<>();
        BigDecimal monthlyInterestRate = loan.getInterestRate(); // Já é mensal (ex: 0.20 para 20%)
        
        // Para pagamento flexível, criamos parcelas com valores variados
        // O cliente pode pagar conforme sua disponibilidade
        for (int i = 1; i <= loan.getTermValue(); i++) {
            // Calcular juros sobre o saldo devedor atual
            BigDecimal interestAmount = remainingPrincipal.multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP);
            
            // Para flexível, sugerimos um valor mínimo (juros + parte do principal)
            BigDecimal suggestedPrincipalAmount = remainingPrincipal.divide(BigDecimal.valueOf(loan.getTermValue() - i + 1), 2, RoundingMode.HALF_UP);
            BigDecimal currentPrincipalAmount = suggestedPrincipalAmount;
            
            if (i == loan.getTermValue()) {
                currentPrincipalAmount = remainingPrincipal;
            }
            
            BigDecimal totalDueAmount = currentPrincipalAmount.add(interestAmount);
            
            LoanInstallment installment = createInstallment(loan, i, currentDate, currentPrincipalAmount, interestAmount, totalDueAmount);
            installments.add(installment);
            
            // Atualizar saldo devedor e próxima data
            remainingPrincipal = remainingPrincipal.subtract(currentPrincipalAmount);
            currentDate = calculateNextDate(currentDate, loan.getPaymentFrequency(), loan.getAlternateDaysInterval());
        }
        
        return installments;
    }
    
    private LoanInstallment createInstallment(Loan loan, int installmentNumber, LocalDate dueDate, 
                                             BigDecimal principalAmount, BigDecimal interestAmount, BigDecimal totalDueAmount) {
        LoanInstallment installment = new LoanInstallment();
        installment.setInstallmentNumber(installmentNumber);
        installment.setDueDate(dueDate);
        installment.setPrincipalAmount(principalAmount);
        installment.setInterestAmount(interestAmount);
        installment.setTotalDueAmount(totalDueAmount);
        installment.setPaidAmount(BigDecimal.ZERO);
        installment.setIsPaid(false);
        installment.setLoan(loan);
        return installment;
    }
    
    private LocalDate calculateNextDate(LocalDate currentDate, PaymentFrequency frequency, Integer alternateDaysInterval) {
        switch (frequency) {
            case DAILY:
                return currentDate.plusDays(1);
            case WEEKLY:
                return currentDate.plusWeeks(1);
            case MONTHLY:
                return currentDate.plusMonths(1);
            case ALTERNATE_DAYS:
                return currentDate.plusDays(alternateDaysInterval != null ? alternateDaysInterval : 2);
            default:
                return currentDate.plusMonths(1);
        }
    }
}
