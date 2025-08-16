-- Adicionar campo de comentário de negociação na tabela loan_installments
ALTER TABLE loan_installments 
ADD COLUMN negotiation_comment TEXT;
