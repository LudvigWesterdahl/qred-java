package com.qred.payment.api.payment.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qred.payment.api.payment.dtos.PaymentV1TypeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "CreatePaymentV1Request")
@AllArgsConstructor
@Getter
@Setter
public final class CreatePaymentV1Request {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull
    @JsonProperty("payment_date")
    private LocalDate paymentDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull
    @JsonProperty("amount")
    private BigDecimal amount;

    @NotNull
    @JsonProperty("type")
    private PaymentV1TypeDto type;

    @NotNull
    @JsonProperty("contract_number")
    private String contractNumber;
}

