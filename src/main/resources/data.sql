-- Dados de exemplo para o sistema Finman

-- Inserir usuários
INSERT INTO users (first_name, last_name, phone_number, address, date_of_birth, known_by_whom, situation, created_at, updated_at) VALUES
('João', 'Silva', '(11) 99999-1111', 'Rua das Flores, 123 - São Paulo/SP', '1985-03-15', 'Maria Santos', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Maria', 'Santos', '(11) 99999-2222', 'Av. Paulista, 456 - São Paulo/SP', '1990-07-22', 'João Silva', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Pedro', 'Oliveira', '(11) 99999-3333', 'Rua Augusta, 789 - São Paulo/SP', '1988-11-10', 'Ana Costa', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Ana', 'Costa', '(11) 99999-4444', 'Rua Oscar Freire, 321 - São Paulo/SP', '1992-05-18', 'Pedro Oliveira', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Willian', 'Moreira', '(11) 99999-5555', 'Rua das Palmeiras, 789 - São Paulo/SP', '1985-08-20', 'João Silva', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserir empréstimos
INSERT INTO loans (user_id, loan_amount, interest_rate, start_date, end_date, payment_frequency, payment_type, term_value, status, outstanding_balance, total_paid_amount, created_at, updated_at) VALUES
(5, 1000.00, 0.2000, '2024-12-29', '2025-03-29', 'MONTHLY', 'FIXED_INSTALLMENTS', 3, 'ACTIVE', 950.00, 475.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserir parcelas do empréstimo do Willian
INSERT INTO loan_installments (loan_id, installment_number, due_date, principal_amount, interest_amount, total_due_amount, is_paid, paid_amount, paid_at, created_at, updated_at) VALUES
(1, 1, '2025-01-29', 333.33, 141.67, 475.00, true, 475.00, '2025-01-29 00:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 2, '2025-02-28', 333.33, 141.67, 475.00, false, 550.00, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 3, '2025-03-29', 333.34, 141.66, 475.00, false, 0.00, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
