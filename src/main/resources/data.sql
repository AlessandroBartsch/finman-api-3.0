-- Dados de exemplo para o sistema Finman

-- Inserir usuários
INSERT INTO users (first_name, last_name, email, phone_number, address, date_of_birth, created_at, updated_at) VALUES
('João', 'Silva', 'joao.silva@email.com', '(11) 99999-1111', 'Rua das Flores, 123 - São Paulo/SP', '1985-03-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Maria', 'Santos', 'maria.santos@email.com', '(11) 99999-2222', 'Av. Paulista, 456 - São Paulo/SP', '1990-07-22', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Pedro', 'Oliveira', 'pedro.oliveira@email.com', '(11) 99999-3333', 'Rua Augusta, 789 - São Paulo/SP', '1988-11-10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Ana', 'Costa', 'ana.costa@email.com', '(11) 99999-4444', 'Rua Oscar Freire, 321 - São Paulo/SP', '1992-05-18', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
