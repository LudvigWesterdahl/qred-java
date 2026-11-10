package com.qred.payment.api.payment.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Schema(description = "PaymentV1TypeDto", enumAsRef = true)
@RequiredArgsConstructor
public enum PaymentV1TypeDto {
    INCOMING("incoming"),
    OUTGOING("outgoing");

    private final String stringValue;

    private static final Map<String, PaymentV1TypeDto> STRING_TO_ENUM
            = Arrays.stream(PaymentV1TypeDto.values()).collect(Collectors.toMap(e -> e.toString(), e -> e));

    public static Optional<PaymentV1TypeDto> fromString(final String string) {
        final PaymentV1TypeDto enumValue = STRING_TO_ENUM.get(string);
        if (enumValue == null) {
            return Optional.empty();
        }

        return Optional.of(enumValue);
    }

    @Override
    public String toString() {
        return stringValue;
    }
}

