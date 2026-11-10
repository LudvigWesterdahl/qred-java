package com.qred.payment.api;

import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public final class DomainException extends RuntimeException {

    private final String errorCode;

    public DomainException(final String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}

