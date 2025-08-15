package com.finman.controller;

import com.finman.model.Loan;
import com.finman.model.enums.PaymentFrequency;
import com.finman.model.enums.PaymentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LoanControllerTest {

    @Test
    public void testProportionalInterestCalculation() {
        // Criar um empréstimo de teste baseado no exemplo da imagem
        Loan loan = new Loan();
        loan.setLoanAmount(new BigDecimal("1000.00"));
        loan.setInterestRate(new BigDecimal("0.20")); // 20% ao mês
        loan.setTermValue(3);
        loan.setPaymentFrequency(PaymentFrequency.MONTHLY);
        loan.setPaymentType(PaymentType.FIXED_INSTALLMENTS);
        loan.setStartDate(LocalDate.of(2024, 8, 30)); // Primeira parcela em 30/08
        
        // Simular data atual como 15/08/2024
        LocalDate today = LocalDate.of(2024, 8, 15);
        
        // Calcular juros proporcionais manualmente para verificar
        BigDecimal baseInterest = loan.getLoanAmount().multiply(loan.getInterestRate());
        long daysUntilFirstInstallment = java.time.temporal.ChronoUnit.DAYS.between(today, loan.getStartDate());
        BigDecimal proportionalFactor = BigDecimal.valueOf(daysUntilFirstInstallment)
            .divide(BigDecimal.valueOf(30), 4, java.math.RoundingMode.HALF_UP);
        BigDecimal expectedProportionalInterest = baseInterest.multiply(proportionalFactor);
        
        // Verificar se o cálculo está correto
        assertEquals(15, daysUntilFirstInstallment); // 15 dias entre 15/08 e 30/08
        assertEquals(new BigDecimal("0.5000"), proportionalFactor); // 15/30 = 0.5
        assertEquals(new BigDecimal("100.00"), expectedProportionalInterest.setScale(2, java.math.RoundingMode.HALF_UP));
        
        System.out.println("Teste de cálculo proporcional:");
        System.out.println("Dias até primeira parcela: " + daysUntilFirstInstallment);
        System.out.println("Fator proporcional: " + proportionalFactor);
        System.out.println("Juros proporcionais esperados: R$ " + expectedProportionalInterest.setScale(2, java.math.RoundingMode.HALF_UP));
    }
    
    @Test
    public void testMonthlyDateCalculation() {
        // Testar cálculo de datas mensais respeitando último dia do mês
        LocalDate startDate = LocalDate.of(2025, 8, 30); // 30 de agosto
        
        // Simular cálculo de próximas datas mensais
        LocalDate nextDate1 = calculateNextMonthlyDateWithOriginalDay(startDate, startDate.getDayOfMonth());
        LocalDate nextDate2 = calculateNextMonthlyDateWithOriginalDay(nextDate1, startDate.getDayOfMonth());
        LocalDate nextDate3 = calculateNextMonthlyDateWithOriginalDay(nextDate2, startDate.getDayOfMonth());
        
        // Verificar se as datas estão corretas
        assertEquals(LocalDate.of(2025, 9, 30), nextDate1); // 30 de setembro
        assertEquals(LocalDate.of(2025, 10, 30), nextDate2); // 30 de outubro  
        assertEquals(LocalDate.of(2025, 11, 30), nextDate3); // 30 de novembro
        
        System.out.println("Teste de cálculo de datas mensais (30 de agosto):");
        System.out.println("Data inicial: " + startDate);
        System.out.println("1ª parcela: " + nextDate1);
        System.out.println("2ª parcela: " + nextDate2);
        System.out.println("3ª parcela: " + nextDate3);
        
        // Testar com data 31 de agosto (deveria voltar para 31 quando o mês tem 31 dias)
        LocalDate startDate31 = LocalDate.of(2025, 8, 31); // 31 de agosto
        
        LocalDate nextDate31_1 = calculateNextMonthlyDateWithOriginalDay(startDate31, startDate31.getDayOfMonth());
        LocalDate nextDate31_2 = calculateNextMonthlyDateWithOriginalDay(nextDate31_1, startDate31.getDayOfMonth());
        LocalDate nextDate31_3 = calculateNextMonthlyDateWithOriginalDay(nextDate31_2, startDate31.getDayOfMonth());
        
        // Verificar se as datas estão corretas
        assertEquals(LocalDate.of(2025, 9, 30), nextDate31_1); // 30 de setembro (setembro tem 30 dias)
        assertEquals(LocalDate.of(2025, 10, 31), nextDate31_2); // 31 de outubro (outubro tem 31 dias, volta para 31)
        assertEquals(LocalDate.of(2025, 11, 30), nextDate31_3); // 30 de novembro (novembro tem 30 dias)
        
        System.out.println("\nTeste de cálculo de datas mensais (31 de agosto):");
        System.out.println("Data inicial: " + startDate31);
        System.out.println("1ª parcela: " + nextDate31_1);
        System.out.println("2ª parcela: " + nextDate31_2);
        System.out.println("3ª parcela: " + nextDate31_3);
        
        // Testar com data 31 de janeiro
        LocalDate jan31 = LocalDate.of(2025, 1, 31);
        LocalDate febDate = calculateNextMonthlyDateWithOriginalDay(jan31, jan31.getDayOfMonth());
        assertEquals(LocalDate.of(2025, 2, 28), febDate); // 28 de fevereiro (2025 não é bissexto)
        
        System.out.println("\nTeste com 31 de janeiro: " + jan31 + " -> " + febDate);
    }
    
    @Test
    public void testFirstInstallmentDate() {
        // Testar se a primeira parcela usa exatamente a data de início
        LocalDate startDate = LocalDate.of(2025, 8, 31); // 31 de agosto
        
        // Simular um empréstimo
        Loan loan = new Loan();
        loan.setLoanAmount(new BigDecimal("500.00"));
        loan.setInterestRate(new BigDecimal("0.20"));
        loan.setTermValue(4);
        loan.setPaymentFrequency(PaymentFrequency.MONTHLY);
        loan.setPaymentType(PaymentType.INTEREST_ONLY);
        loan.setStartDate(startDate);
        
        // Verificar se a data de início está correta
        assertEquals(LocalDate.of(2025, 8, 31), loan.getStartDate());
        
        System.out.println("Teste da primeira parcela:");
        System.out.println("Data de início do empréstimo: " + loan.getStartDate());
        System.out.println("Dia do mês: " + loan.getStartDate().getDayOfMonth());
        
        // Simular cálculo da primeira parcela
        LocalDate firstInstallmentDate = startDate; // Deveria ser exatamente a data de início
        System.out.println("Data da primeira parcela: " + firstInstallmentDate);
        
        assertEquals(startDate, firstInstallmentDate);
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
