package com.vanessaviagem.backoffice.domain.model;

import java.math.BigDecimal;

/**
 * Resultado de uma operação de venda de milhas.
 */
public record ResultadoVenda(
        ContaPrograma contaAtualizada,
        BigDecimal custoRemovido,
        BigDecimal lucro
) {}
