package com.qred.payment.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    /**
     * Supported by index {@code uq_contract_contract_number} and {@code idx_payment_contract}.
     */
    List<PaymentEntity> findAllByContractContractNumber(String contractNumber);
}
