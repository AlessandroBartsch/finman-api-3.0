-- Migration V1.6: Adicionar campo para rastrear juros de atraso pagos
ALTER TABLE loan_installments 
ADD COLUMN overdue_interest_paid DECIMAL(18,2) DEFAULT 0.00;
