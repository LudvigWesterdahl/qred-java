package com.qred.payment.data;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.function.Consumer;

@Component
public final class TransactionHelper {

    private final TransactionTemplate transactionTemplate;

    public TransactionHelper(final PlatformTransactionManager platformTransactionManager) {
        Objects.requireNonNull(platformTransactionManager);

        transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    public void executeWithoutResult(final Consumer<TransactionStatus> action) {
        transactionTemplate.executeWithoutResult(action);
    }

    public <T> T execute(final TransactionCallback<T> action) {
        return transactionTemplate.execute(action);
    }
}

