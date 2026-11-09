package com.qred.payment.api.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentV1Response(
        Long id,
        LocalDate paymentDate,
        BigDecimal amount,
        String type,
        String contractNumber,
        String clientNumber
) {
}
