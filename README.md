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
│   └── UserController.java            # API de usuários
├── model/                             # Entidades JPA
│   ├── enums/                         # Enums do sistema
│   │   ├── LoanStatus.java
│   │   ├── PaymentFrequency.java
│   │   ├── TransactionType.java
│   │   ├── CashMovementType.java
│   │   └── CashRegisterSessionStatus.java
│   ├── User.java                      # Usuários do sistema
│   ├── Loan.java                      # Empréstimos
│   ├── LoanInstallment.java           # Parcelas dos empréstimos
│   ├── Transaction.java               # Transações financeiras
│   ├── CashRegister.java              # Caixas físicos/lógicos
│   ├── CashRegisterSession.java       # Sessões de caixa
│   └── CashMovement.java              # Movimentos de caixa
└── repository/                        # Repositórios JPA
    └── UserRepository.java
```

## 🗄️ Modelo de Dados

### Entidades Principais

1. **User** - Usuários do sistema
2. **Loan** - Empréstimos com status, juros e parcelas
3. **LoanInstallment** - Parcelas individuais dos empréstimos
4. **Transaction** - Transações financeiras (pagamentos, desembolsos, etc.)
5. **CashRegister** - Caixas físicos/lógicos
6. **CashRegisterSession** - Sessões de abertura/fechamento de caixa
7. **CashMovement** - Movimentos de entrada/saída de dinheiro

### Enums

- **LoanStatus**: PENDING, APPROVED, ACTIVE, PAID, DEFAULTED, REJECTED, CANCELLED
- **PaymentFrequency**: DAILY, WEEKLY, MONTHLY, ALTERNATE_DAYS
- **TransactionType**: DISBURSEMENT, PAYMENT, FEE, PENALTY, REFUND
- **CashMovementType**: INFLOW, OUTFLOW
- **CashRegisterSessionStatus**: OPEN, CLOSED

## 🔧 APIs Disponíveis

### Usuários (`/api/users`)
- `GET /api/users` - Listar todos os usuários
- `GET /api/users/{id}` - Buscar usuário por ID
- `POST /api/users` - Criar novo usuário
- `PUT /api/users/{id}` - Atualizar usuário
- `DELETE /api/users/{id}` - Deletar usuário

## 📊 Dados de Exemplo

O sistema já vem com dados de exemplo carregados automaticamente:

- **4 usuários** (João, Maria, Pedro, Ana)

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
    "dateOfBirth": "1985-08-20"
  }'
```

## 📝 Próximos Passos

1. Implementar controllers para empréstimos e caixa
2. Adicionar validações mais robustas
3. Implementar autenticação e autorização
4. Adicionar testes unitários e de integração
5. Implementar relatórios financeiros
6. Criar interface web

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
- 🔄 Controllers de empréstimos e caixa (próximo passo)
- 🔄 Validações e tratamento de erros
- 🔄 Testes automatizados
