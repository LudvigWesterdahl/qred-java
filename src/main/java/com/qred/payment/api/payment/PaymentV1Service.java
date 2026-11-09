package com.qred.payment.api.payment;

import com.qred.payment.data.PaymentEntity;
import com.qred.payment.data.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentV1Service {

    private final PaymentRepository paymentRepository;

    public List<PaymentV1Response> list(String contractNumber) {
        List<PaymentEntity> result = contractNumber == null
                ? paymentRepository.findAllByOrderByIdAsc()
                : paymentRepository.findByContract_ContractNumberOrderByIdAsc(contractNumber);
        // return result.stream().map(this::toResponse).toList();
        System.out.println(result.toString());

        final PaymentV1Response paymentResponse = new PaymentV1Response(
                123L,
                LocalDate.now(),
                new BigDecimal("500"),
                "type",
                "contract",
                "client1"
                );

        return List.of(paymentResponse);
    }

    private PaymentV1Response toResponse(PaymentEntity payment) {
        return new PaymentV1Response(
                payment.getId(),
                payment.getPaymentDate(),
                payment.getAmount(),
                payment.getType(),
                payment.getContract().getContractNumber(),
                payment.getContract().getClient().getClientNumber()
        );
    }
}
