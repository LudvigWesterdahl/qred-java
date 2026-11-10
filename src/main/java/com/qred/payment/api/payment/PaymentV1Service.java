package com.qred.payment.api.payment;

import com.qred.payment.api.DomainException;
import com.qred.payment.api.payment.dtos.PaymentV1TypeDto;
import com.qred.payment.api.payment.requests.CreatePaymentV1Request;
import com.qred.payment.api.payment.requests.FilterPaymentV1Request;
import com.qred.payment.api.payment.responses.PaymentV1Response;
import com.qred.payment.data.ContractEntity;
import com.qred.payment.data.ContractRepository;
import com.qred.payment.data.PaymentEntity;
import com.qred.payment.data.PaymentRepository;
import com.qred.payment.data.TransactionHelper;
import com.qred.payment.helpers.CurrentDateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentV1Service {

    private static final Logger log = LoggerFactory.getLogger(PaymentV1Service.class);

    private final PaymentRepository paymentRepository;
    private final ContractRepository contractRepository;
    private final TransactionHelper transactionHelper;
    private final CurrentDateHelper currentDateHelper;

    private static PaymentV1Response toResponse(final PaymentEntity paymentEntity) {
        return new PaymentV1Response(
                paymentEntity.getId(),
                paymentEntity.getPaymentDate(),
                paymentEntity.getAmount(),
                PaymentV1TypeDto.fromString(paymentEntity.getType()).orElseThrow(),
                paymentEntity.getContract().getClient().getClientNumber(),
                paymentEntity.getContract().getContractNumber()
        );
    }

    private static List<PaymentV1Response> toResponses(final Collection<? extends PaymentEntity> paymentEntitys) {
        Objects.requireNonNull(paymentEntitys);

        final List<PaymentV1Response> result = new ArrayList<>(paymentEntitys.size());
        for (final PaymentEntity paymentEntity : paymentEntitys) {
            Objects.requireNonNull(paymentEntity);

            result.add(toResponse(paymentEntity));
        }

        return result;
    }

    public List<PaymentV1Response> getPayments(final FilterPaymentV1Request request) {
        Objects.requireNonNull(request);

        return transactionHelper.execute(transactionStatus -> {
            final List<PaymentEntity> paymentEntities = paymentRepository.findAllByContractContractNumber(request.getContract_number());
            return toResponses(paymentEntities);
        });
    }

    private void createPaymentInTransaction(final CreatePaymentV1Request request) {
        final ContractEntity contractEntity = contractRepository.findByContractNumber(request.getContractNumber())
                .orElse(null);

        if (contractEntity == null) {
            throw new DomainException("CONTRACT_MISSING");
        }

        final PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setPaymentDate(request.getPaymentDate());
        paymentEntity.setAmount(request.getAmount());
        paymentEntity.setType(request.getType().toString());
        paymentEntity.setContract(contractEntity);

        paymentRepository.save(paymentEntity);

        log.info("created payment [id={}]", paymentEntity.getId());
    }

    public void createPayment(final CreatePaymentV1Request request) {
        Objects.requireNonNull(request);

        if (request.getPaymentDate().isAfter(currentDateHelper.getLocalDateNow())) {
            throw new DomainException("PAYMENT_DATE_FUTURE");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("AMOUNT_NON_POSITIVE");
        }

        try {
            request.getAmount().setScale(2, RoundingMode.UNNECESSARY);
        } catch (@SuppressWarnings("unused") final ArithmeticException ignored) {
            throw new DomainException("AMOUNT_TOO_MANY_DECIMALS");
        }

        transactionHelper.executeWithoutResult(transactionStatus -> createPaymentInTransaction(request));
    }
}
