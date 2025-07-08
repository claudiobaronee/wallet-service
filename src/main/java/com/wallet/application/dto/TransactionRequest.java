package com.wallet.application.dto;

import com.wallet.domain.valueobjects.Money;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisições de transação (depósito, saque).
 * Segue especificação OpenAPI para API-First design.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private Double amount;
    
    @NotBlank(message = "Moeda é obrigatória")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Moeda deve seguir padrão ISO 4217 (ex: BRL, USD)")
    private String currency;
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;
    
    /**
     * Converte para objeto de domínio Money.
     */
    public Money toMoney() {
        return new Money(amount, currency);
    }
    
    /**
     * Método para compatibilidade com transferências.
     * Em uma implementação real, isso seria um campo separado.
     */
    public String getTargetUserId() {
        return null; // Não implementado nesta versão
    }
} 