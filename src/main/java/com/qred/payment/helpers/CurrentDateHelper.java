package com.qred.payment.helpers;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
@AllArgsConstructor
public final class CurrentDateHelper {

    private Instant now;

    public static LocalDate toLocalDate(final Instant instant) {
        return LocalDate.ofInstant(instant, ZoneOffset.UTC);
    }

    public Instant getInstantNow() {
        if (now == null) {
            return Instant.now();
        }

        return now;
    }

    public LocalDate getLocalDateNow() {
        return toLocalDate(getInstantNow());
    }
}

