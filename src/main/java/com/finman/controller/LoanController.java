package com.finman.controller;

import com.finman.model.Loan;
import com.finman.model.User;
import com.finman.model.LoanInstallment;
import com.finman.model.enums.LoanStatus;
import com.finman.model.enums.PaymentFrequency;
import com.finman.model.enums.PaymentType;
import com.finman.repository.LoanRepository;
import com.finman.repository.UserRepository;
import com.finman.repository.LoanInstallmentRepository;
import com.finman.dto.CreateLoanRequest;
import com.finman.dto.UpdateLoanRequest;
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
    
    @Autowired
    private LoanInstallmentRepository loanInstallmentRepository;
    
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
        loan.setChargeInterestSeparately(request.getChargeInterestSeparately() != null ? request.getChargeInterestSeparately() : false);
        
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
        
        // Criar parcelas automaticamente após aprovação
        createInstallmentsForLoan(savedLoan);
        
        return ResponseEntity.ok(savedLoan);
    }
    
    @PutMapping("/{id}/disburse")
    public ResponseEntity<Loan> disburseLoan(@PathVariable Long id) {
        Optional<Loan> loanOpt = loanRepository.findById(id);
        
        if (!loanOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Loan loan = loanOpt.get();
        
        // Só permite liberar se estiver aprovado
        if (loan.getStatus() != LoanStatus.APPROVED) {
            return ResponseEntity.badRequest().build();
        }
        
        loan.disburse();
        Loan savedLoan = loanRepository.save(loan);
        
        return ResponseEntity.ok(savedLoan);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Loan> updateLoan(@PathVariable Long id, @RequestBody UpdateLoanRequest request) {
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
        loan.setChargeInterestSeparately(request.getChargeInterestSeparately() != null ? request.getChargeInterestSeparately() : false);
        
        // Atualizar status se fornecido
        if (request.getStatus() != null) {
            loan.setStatus(request.getStatus());
        }
        
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
    
    @PutMapping("/{id}/revert")
    public ResponseEntity<Loan> revertLoan(@PathVariable Long id) {
        Optional<Loan> loanOpt = loanRepository.findById(id);
        
        if (!loanOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Loan loan = loanOpt.get();
        
        // Só permite reverter se estiver aprovado
        if (loan.getStatus() != LoanStatus.APPROVED) {
            return ResponseEntity.badRequest().build();
        }
        
        loan.revertToPending();
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
                // Para pagamentos mensais, respeitar o último dia do mês
                endDate = calculateEndDateWithOriginalDay(startDate, termValue);
                break;
            case ALTERNATE_DAYS:
                // Para dias alternados, assumimos que termValue é o número de parcelas
                // e cada parcela é a cada 2 dias
                endDate = startDate.plusDays((termValue - 1) * 2);
                break;
        }
        
        return endDate;
    }
    
    private LocalDate calculateEndDateWithOriginalDay(LocalDate startDate, Integer termValue) {
        // Rastrear o dia original do empréstimo
        int originalDayOfMonth = startDate.getDayOfMonth();
        LocalDate currentDate = startDate;
        
        // Calcular a data final usando a lógica que respeita o dia original
        for (int i = 1; i < termValue; i++) {
            currentDate = calculateNextMonthlyDateWithOriginalDay(currentDate, originalDayOfMonth);
        }
        
        return currentDate;
    }
    
    private BigDecimal calculateProportionalInterest(BigDecimal baseInterestAmount, LocalDate installmentDate, LocalDate currentDate, int installmentNumber, LocalDate previousInstallmentDate) {
        // Para a primeira parcela, calcular proporcionalidade entre data atual e data da parcela
        if (installmentNumber == 1) {
            // Se a data da parcela é igual à data atual, não há proporcionalidade
            if (installmentDate.equals(currentDate)) {
                return baseInterestAmount;
            }
            
            // Calcular dias entre a data atual e a data da parcela
            long daysUntilInstallment = java.time.temporal.ChronoUnit.DAYS.between(currentDate, installmentDate);
            
            // Se não há diferença de dias ou é negativo, retornar o valor original
            if (daysUntilInstallment <= 0) {
                return baseInterestAmount;
            }
            
            // Aplicar proporcionalidade: (juros mensais × dias) ÷ 30
            BigDecimal proportionalFactor = BigDecimal.valueOf(daysUntilInstallment)
                .divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_UP);
            
            return baseInterestAmount.multiply(proportionalFactor).setScale(2, RoundingMode.HALF_UP);
        } else {
            // Para parcelas subsequentes, calcular proporcionalidade entre a data da parcela anterior e a atual
            if (previousInstallmentDate == null) {
                return baseInterestAmount;
            }
            
            // Calcular dias entre a data da parcela anterior e a data da parcela atual
            long daysBetweenInstallments = java.time.temporal.ChronoUnit.DAYS.between(previousInstallmentDate, installmentDate);
            
            // Se não há diferença de dias ou é negativo, retornar o valor original
            if (daysBetweenInstallments <= 0) {
                return baseInterestAmount;
            }
            
            // Aplicar proporcionalidade: (juros mensais × dias) ÷ 30
            BigDecimal proportionalFactor = BigDecimal.valueOf(daysBetweenInstallments)
                .divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_UP);
            
            return baseInterestAmount.multiply(proportionalFactor).setScale(2, RoundingMode.HALF_UP);
        }
    }
    
    private void createInstallmentsForLoan(Loan loan) {
        // Gerar parcelas usando a mesma lógica da simulação
        List<LoanInstallment> installments = generateSimulatedInstallments(loan);
        
        // Calcular totais
        BigDecimal totalInterest = installments.stream()
            .map(LoanInstallment::getInterestAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal totalLoanValue = installments.stream()
            .map(LoanInstallment::getTotalDueAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Atualizar os totais no empréstimo
        loan.setTotalInterest(totalInterest);
        loan.setTotalLoanValue(totalLoanValue);
        
        // Salvar as parcelas no banco de dados
        for (LoanInstallment installment : installments) {
            installment.setLoan(loan);
            loanInstallmentRepository.save(installment);
        }
        
        // Salvar o empréstimo atualizado
        loanRepository.save(loan);
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
        
        // Obter data atual para cálculo proporcional da primeira parcela
        LocalDate today = LocalDate.now();
        LocalDate previousInstallmentDate = null;
        
        // Rastrear o dia original do empréstimo
        int originalDayOfMonth = currentDate.getDayOfMonth();
        
        for (int i = 1; i <= loan.getTermValue(); i++) {
            // Calcular juros sobre o saldo devedor atual
            BigDecimal baseInterestAmount = remainingPrincipal.multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP);
            
            // Aplicar proporcionalidade baseada em dias
            BigDecimal interestAmount = calculateProportionalInterest(baseInterestAmount, currentDate, today, i, previousInstallmentDate);
            
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
            
            // Atualizar saldo devedor, próxima data e data da parcela anterior
            remainingPrincipal = remainingPrincipal.subtract(currentPrincipalAmount);
            previousInstallmentDate = currentDate;
            currentDate = calculateNextMonthlyDateWithOriginalDay(currentDate, originalDayOfMonth);
        }
        
        return installments;
    }
    
    private List<LoanInstallment> generateInterestOnlyInstallments(Loan loan, BigDecimal remainingPrincipal, LocalDate currentDate) {
        List<LoanInstallment> installments = new ArrayList<>();
        BigDecimal monthlyInterestRate = loan.getInterestRate(); // Já é mensal (ex: 0.20 para 20%)
        
        // Obter data atual para cálculo proporcional da primeira parcela
        LocalDate today = LocalDate.now();
        LocalDate previousInstallmentDate = null;
        
        // Rastrear o dia original do empréstimo
        int originalDayOfMonth = currentDate.getDayOfMonth();
        
        for (int i = 1; i <= loan.getTermValue(); i++) {
            // Calcular juros sobre o saldo devedor atual
            BigDecimal baseInterestAmount = remainingPrincipal.multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP);
            
            // Aplicar proporcionalidade baseada em dias
            BigDecimal interestAmount = calculateProportionalInterest(baseInterestAmount, currentDate, today, i, previousInstallmentDate);
            
            // Para as parcelas normais, só paga juros
            BigDecimal currentPrincipalAmount = BigDecimal.ZERO;
            
            if (i == loan.getTermValue()) {
                // Na última parcela
                if (loan.getChargeInterestSeparately() != null && loan.getChargeInterestSeparately()) {
                    // Criar duas parcelas separadas: uma para juros e outra para principal
                    
                    // Parcela 1: Apenas juros
                    LoanInstallment interestInstallment = createInstallment(loan, i, currentDate, BigDecimal.ZERO, interestAmount, interestAmount);
                    installments.add(interestInstallment);
                    
                    // Parcela 2: Apenas principal
                    LoanInstallment principalInstallment = createInstallment(loan, i + 1, currentDate, remainingPrincipal, BigDecimal.ZERO, remainingPrincipal);
                    installments.add(principalInstallment);
                    
                    // Atualizar saldo devedor
                    remainingPrincipal = BigDecimal.ZERO;
                } else {
                    // Comportamento original: principal + juros na mesma parcela
                    currentPrincipalAmount = remainingPrincipal;
                }
            }
            
            if (i < loan.getTermValue() || (loan.getChargeInterestSeparately() == null || !loan.getChargeInterestSeparately())) {
                BigDecimal totalDueAmount = currentPrincipalAmount.add(interestAmount);
                
                LoanInstallment installment = createInstallment(loan, i, currentDate, currentPrincipalAmount, interestAmount, totalDueAmount);
                installments.add(installment);
                
                // Atualizar saldo devedor
                remainingPrincipal = remainingPrincipal.subtract(currentPrincipalAmount);
            }
            
            // Atualizar próxima data e data da parcela anterior
            previousInstallmentDate = currentDate;
            currentDate = calculateNextMonthlyDateWithOriginalDay(currentDate, originalDayOfMonth);
        }
        
        return installments;
    }
    
    private List<LoanInstallment> generateFlexibleInstallments(Loan loan, BigDecimal remainingPrincipal, LocalDate currentDate) {
        List<LoanInstallment> installments = new ArrayList<>();
        BigDecimal monthlyInterestRate = loan.getInterestRate(); // Já é mensal (ex: 0.20 para 20%)
        
        // Obter data atual para cálculo proporcional da primeira parcela
        LocalDate today = LocalDate.now();
        LocalDate previousInstallmentDate = null;
        
        // Rastrear o dia original do empréstimo
        int originalDayOfMonth = currentDate.getDayOfMonth();
        
        // Para pagamento flexível, criamos parcelas com valores variados
        // O cliente pode pagar conforme sua disponibilidade
        for (int i = 1; i <= loan.getTermValue(); i++) {
            // Calcular juros sobre o saldo devedor atual
            BigDecimal baseInterestAmount = remainingPrincipal.multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP);
            
            // Aplicar proporcionalidade baseada em dias
            BigDecimal interestAmount = calculateProportionalInterest(baseInterestAmount, currentDate, today, i, previousInstallmentDate);
            
            // Para flexível, sugerimos um valor mínimo (juros + parte do principal)
            BigDecimal suggestedPrincipalAmount = remainingPrincipal.divide(BigDecimal.valueOf(loan.getTermValue() - i + 1), 2, RoundingMode.HALF_UP);
            BigDecimal currentPrincipalAmount = suggestedPrincipalAmount;
            
            if (i == loan.getTermValue()) {
                // Na última parcela
                if (loan.getChargeInterestSeparately() != null && loan.getChargeInterestSeparately()) {
                    // Criar duas parcelas separadas: uma para juros e outra para principal
                    
                    // Parcela 1: Apenas juros
                    LoanInstallment interestInstallment = createInstallment(loan, i, currentDate, BigDecimal.ZERO, interestAmount, interestAmount);
                    installments.add(interestInstallment);
                    
                    // Parcela 2: Apenas principal
                    LoanInstallment principalInstallment = createInstallment(loan, i + 1, currentDate, remainingPrincipal, BigDecimal.ZERO, remainingPrincipal);
                    installments.add(principalInstallment);
                    
                    // Atualizar saldo devedor
                    remainingPrincipal = BigDecimal.ZERO;
                } else {
                    // Comportamento original: principal + juros na mesma parcela
                    currentPrincipalAmount = remainingPrincipal;
                }
            }
            
            if (i < loan.getTermValue() || (loan.getChargeInterestSeparately() == null || !loan.getChargeInterestSeparately())) {
                BigDecimal totalDueAmount = currentPrincipalAmount.add(interestAmount);
                
                LoanInstallment installment = createInstallment(loan, i, currentDate, currentPrincipalAmount, interestAmount, totalDueAmount);
                installments.add(installment);
                
                // Atualizar saldo devedor
                remainingPrincipal = remainingPrincipal.subtract(currentPrincipalAmount);
            }
            
            // Atualizar próxima data e data da parcela anterior
            previousInstallmentDate = currentDate;
            currentDate = calculateNextMonthlyDateWithOriginalDay(currentDate, originalDayOfMonth);
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
                // Para pagamentos mensais, respeitar o último dia do mês
                return calculateNextMonthlyDate(currentDate);
            case ALTERNATE_DAYS:
                return currentDate.plusDays(alternateDaysInterval != null ? alternateDaysInterval : 2);
            default:
                return calculateNextMonthlyDate(currentDate);
        }
    }
    
    private LocalDate calculateNextMonthlyDate(LocalDate currentDate) {
        // Obter o dia do mês da data atual
        int dayOfMonth = currentDate.getDayOfMonth();
        
        // Adicionar 1 mês
        LocalDate nextMonth = currentDate.plusMonths(1);
        
        // Verificar se o mês seguinte tem o mesmo dia
        int daysInNextMonth = nextMonth.lengthOfMonth();
        
        if (dayOfMonth > daysInNextMonth) {
            // Se o dia não existe no próximo mês, usar o último dia do mês
            return nextMonth.withDayOfMonth(daysInNextMonth);
        } else {
            // Se o dia existe, usar o mesmo dia
            return nextMonth.withDayOfMonth(dayOfMonth);
        }
    }
    
    private LocalDate calculateNextMonthlyDateWithOriginalDay(LocalDate currentDate, int originalDayOfMonth) {
        // Adicionar 1 mês
        LocalDate nextMonth = currentDate.plusMonths(1);
        
        // Verificar se o mês seguinte tem o dia original
        int daysInNextMonth = nextMonth.lengthOfMonth();
        
        if (originalDayOfMonth > daysInNextMonth) {
            // Se o dia original não existe no próximo mês, usar o último dia do mês
            return nextMonth.withDayOfMonth(daysInNextMonth);
        } else {
            // Se o dia original existe, usar o dia original
            return nextMonth.withDayOfMonth(originalDayOfMonth);
        }
    }
}
