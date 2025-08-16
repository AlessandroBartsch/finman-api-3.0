-- Adicionar campos para cálculo de atraso na tabela loan_installments
ALTER TABLE loan_installments 
ADD COLUMN daily_interest_rate DECIMAL(5,2) DEFAULT 100.00,
ADD COLUMN overdue_days INT DEFAULT 0,
ADD COLUMN daily_interest_amount DECIMAL(18,2) DEFAULT 0.00,
ADD COLUMN overdue_interest_amount DECIMAL(18,2) DEFAULT 0.00,
ADD COLUMN total_with_overdue DECIMAL(18,2) DEFAULT 0.00;

-- Atualizar total_with_overdue para ser igual ao total_due_amount inicialmente
UPDATE loan_installments 
SET total_with_overdue = total_due_amount 
WHERE total_with_overdue = 0.00;
