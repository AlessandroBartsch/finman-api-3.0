package com.finman.controller;

import com.finman.repository.UserRepository;
import com.finman.repository.LoanRepository;
import com.finman.repository.DocumentRepository;
import com.finman.repository.LoanInstallmentRepository;
import com.finman.model.enums.LoanStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private LoanInstallmentRepository loanInstallmentRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Total de clientes
            long totalUsers = userRepository.count();
            stats.put("totalUsers", totalUsers);
            
            // Empréstimos ativos
            long activeLoans = loanRepository.findByStatus(LoanStatus.ACTIVE).size();
            stats.put("activeLoans", activeLoans);
            
            // Valor total dos empréstimos ativos
            BigDecimal totalValue = loanRepository.findByStatus(LoanStatus.ACTIVE)
                .stream()
                .map(loan -> loan.getLoanAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.put("totalValue", totalValue);
            
            // Total de documentos
            long totalDocuments = documentRepository.count();
            stats.put("totalDocuments", totalDocuments);
            
            // Parcelas pagas (todas)
            long paidInstallments = loanInstallmentRepository.findAll()
                .stream()
                .filter(installment -> installment.getIsPaid())
                .count();
            stats.put("paidInstallments", paidInstallments);
            
            // Parcelas vencidas
            long overdueInstallments = loanInstallmentRepository.findByDueDateBeforeAndIsPaidFalse(
                java.time.LocalDate.now()
            ).size();
            stats.put("overdueInstallments", overdueInstallments);
            
            // Empréstimos aprovados (todos)
            long approvedLoans = loanRepository.findByStatus(LoanStatus.APPROVED).size();
            stats.put("approvedLoans", approvedLoans);
            
            // Valor total aprovado
            BigDecimal totalApproved = loanRepository.findByStatus(LoanStatus.APPROVED)
            .stream()
            .map(loan -> loan.getLoanAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.put("totalApproved", totalApproved);
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erro ao buscar estatísticas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
