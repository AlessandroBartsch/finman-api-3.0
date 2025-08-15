package com.finman.model.enums;

public enum PaymentType {
    FIXED_INSTALLMENTS,    // Parcelas fixas (principal + juros)
    INTEREST_ONLY,         // Só juros mensais (principal no final)
    FLEXIBLE              // Flexível (pagamento personalizado)
}
