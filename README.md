# Finman API 3.0

API de Gerenciamento Financeiro desenvolvida com Spring Boot, focada em controle de empréstimos e sistema de caixa.

## 🚀 Tecnologias Utilizadas

- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (banco em memória para testes)
- **Maven**
- **Java 17**

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+

## 🛠️ Como Executar

1. **Clone o repositório:**
   ```bash
   git clone <url-do-repositorio>
   cd finman-api-3.0
   ```

2. **Execute a aplicação:**
   ```bash
   mvn spring-boot:run
   ```

3. **Acesse a aplicação:**
   - API: http://localhost:8080
   - Console H2: http://localhost:8080/h2-console
     - JDBC URL: `jdbc:h2:mem:finman_db`
     - Username: `sa`
     - Password: `password`

## 📁 Estrutura do Projeto

```
src/main/java/com/finman/
├── FinmanApiApplication.java           # Classe principal
├── controller/                         # Controllers REST
│   ├── UserController.java            # API de usuários
│   ├── LoanController.java            # API de empréstimos
│   ├── LoanInstallmentController.java # API de parcelas
│   ├── DocumentController.java        # API de documentos
│   └── DashboardController.java       # API de dashboard
├── model/                             # Entidades JPA
│   ├── enums/                         # Enums do sistema
│   │   ├── LoanStatus.java
│   │   ├── PaymentFrequency.java
│   │   ├── PaymentType.java
│   │   ├── TransactionType.java
│   │   ├── CashMovementType.java
│   │   ├── CashRegisterSessionStatus.java
│   │   ├── UserSituation.java
│   │   └── DocumentType.java
│   ├── User.java                      # Usuários do sistema
│   ├── Loan.java                      # Empréstimos
│   ├── LoanInstallment.java           # Parcelas dos empréstimos
│   ├── Transaction.java               # Transações financeiras
│   ├── Document.java                  # Documentos dos usuários
│   ├── CashRegister.java              # Caixas físicos/lógicos
│   ├── CashRegisterSession.java       # Sessões de caixa
│   └── CashMovement.java              # Movimentos de caixa
├── repository/                        # Repositórios JPA
│   ├── UserRepository.java
│   ├── LoanRepository.java
│   ├── LoanInstallmentRepository.java
│   └── DocumentRepository.java
├── service/                           # Serviços
│   └── FileStorageService.java        # Serviço de armazenamento de arquivos
└── dto/                               # Data Transfer Objects
    ├── CreateLoanRequest.java
    ├── UpdateLoanRequest.java
    └── SimulateInstallmentsResponse.java
```

## 🗄️ Modelo de Dados

### Entidades Principais

1. **User** - Usuários do sistema
2. **Loan** - Empréstimos com status, juros e parcelas
3. **LoanInstallment** - Parcelas individuais dos empréstimos
4. **Transaction** - Transações financeiras (pagamentos, desembolsos, etc.)
5. **Document** - Documentos dos usuários (RG, CPF, comprovantes, etc.)
6. **CashRegister** - Caixas físicos/lógicos
7. **CashRegisterSession** - Sessões de abertura/fechamento de caixa
8. **CashMovement** - Movimentos de entrada/saída de dinheiro

### Enums

- **LoanStatus**: PENDING, APPROVED, ACTIVE, PAID, DEFAULTED, REJECTED, CANCELLED
- **PaymentFrequency**: DAILY, WEEKLY, MONTHLY, ALTERNATE_DAYS
- **TransactionType**: DISBURSEMENT, PAYMENT, FEE, PENALTY, REFUND
- **CashMovementType**: INFLOW, OUTFLOW
- **CashRegisterSessionStatus**: OPEN, CLOSED
- **UserSituation**: ACTIVE, DEACTIVATED
- **DocumentType**: RG, CPF, COMPROVANTE_RESIDENCIA, COMPROVANTE_RENDA, CONTRACHEQUE, EXTRATO_BANCARIO, ASSINATURA_PROMISSORIA, OUTROS

## 🔧 APIs Disponíveis

### Usuários (`/api/users`)
- `GET /api/users` - Listar todos os usuários
- `GET /api/users/{id}` - Buscar usuário por ID
- `POST /api/users` - Criar novo usuário
- `PUT /api/users/{id}` - Atualizar usuário
- `DELETE /api/users/{id}` - Deletar usuário

### Empréstimos (`/api/loans`)
- `GET /api/loans` - Listar todos os empréstimos
- `GET /api/loans/{id}` - Buscar empréstimo por ID
- `GET /api/loans/user/{userId}` - Buscar empréstimos por usuário
- `GET /api/loans/status/{status}` - Buscar empréstimos por status
- `GET /api/loans/filter` - Filtrar empréstimos por situação
- `POST /api/loans` - Criar novo empréstimo
- `PUT /api/loans/{id}` - Atualizar empréstimo
- `PUT /api/loans/{id}/approve` - Aprovar empréstimo
- `PUT /api/loans/{id}/disburse` - Liberar empréstimo
- `PUT /api/loans/{id}/cancel` - Cancelar empréstimo
- `PUT /api/loans/{id}/revert` - Reverter empréstimo
- `DELETE /api/loans/{id}` - Deletar empréstimo

### Parcelas (`/api/installments`)
- `GET /api/installments` - Listar todas as parcelas
- `GET /api/installments/{id}` - Buscar parcela por ID
- `GET /api/installments/loan/{loanId}` - Buscar parcelas por empréstimo
- `GET /api/installments/loan/{loanId}/overdue` - Buscar parcelas em atraso
- `GET /api/installments/loan/{loanId}/with-overdue-calculation` - Parcelas com cálculo de juros
- `PUT /api/installments/{id}/pay` - Pagar parcela (com comentário opcional)
- `PUT /api/installments/{id}/mark-as-paid` - Marcar parcela como paga
- `PUT /api/installments/{id}/update-daily-interest-rate` - Atualizar taxa de juros diária

**Funcionalidades Especiais:**
- **Cálculo automático de juros de atraso** baseado na taxa diária configurável
- **Rastreamento de excedente** de juros de atraso pagos
- **Comentários de negociação** para parcelas pagas com atraso
- **Informações detalhadas** de pagamento com histórico de valores

### Documentos (`/api/documents`)
- `GET /api/documents/user/{userId}` - Buscar documentos por usuário
- `GET /api/documents/user/{userId}/type/{type}` - Buscar documentos por tipo
- `POST /api/documents/user/{userId}` - Upload de documento
- `GET /api/documents/{id}/download` - Download de documento
- `GET /api/documents/{id}/view` - Visualizar documento
- `PUT /api/documents/{id}/verify` - Verificar documento
- `DELETE /api/documents/{id}` - Deletar documento

### Dashboard (`/api/dashboard`)
- `GET /api/dashboard/stats` - Estatísticas gerais do sistema

## 📊 Dados de Exemplo

O sistema já vem com dados de exemplo carregados automaticamente:

- **5 usuários** (João, Maria, Pedro, Ana, Willian) com informações de indicação e situação

## 🔧 Configurações

O projeto está configurado com:
- Banco H2 em memória para desenvolvimento
- Console H2 habilitado para visualização dos dados
- Logs SQL habilitados para debug
- Validação de dados com Bean Validation
- Dados de exemplo carregados automaticamente

## 🧪 Testando a API

### Exemplo de listagem de usuários:
```bash
curl http://localhost:8080/api/users
```

### Exemplo de criação de usuário:
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Carlos",
    "lastName": "Ferreira",
    "phoneNumber": "(11) 99999-5555",
    "address": "Rua das Palmeiras, 456 - São Paulo/SP",
    "dateOfBirth": "1985-08-20",
    "knownByWhom": "João Silva",
    "situation": "ACTIVE"
  }'
```

## 📝 Próximos Passos

1. ✅ Implementar controllers para empréstimos e documentos
2. ✅ Adicionar validações básicas
3. 🔄 Implementar autenticação e autorização
4. 🔄 Adicionar testes unitários e de integração
5. 🔄 Implementar relatórios financeiros
6. ✅ Criar interface web (frontend React)
7. 🔄 Sistema de caixa (futuro)

## 🧪 Testes

Para executar os testes:
```bash
mvn test
```

## ✅ Status Atual

- ✅ Estrutura básica implementada
- ✅ Todas as entidades criadas
- ✅ Banco H2 configurado
- ✅ API de usuários funcionando
- ✅ Dados de exemplo carregados
- ✅ Sistema de empréstimos com cálculo de juros compostos
- ✅ Controllers de empréstimos implementados
- ✅ Sistema de parcelas com cálculo de juros de atraso
- ✅ Rastreamento de excedente de juros de atraso pagos
- ✅ Comentários de negociação para parcelas
- ✅ Sistema de documentos com upload e download
- ✅ API de dashboard com estatísticas
- ✅ Validações básicas implementadas
- 🔄 Testes automatizados (próximo passo)
- 🔄 Sistema de caixa (futuro)
