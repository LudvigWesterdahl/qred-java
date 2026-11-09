package com.qred.payment.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    List<PaymentEntity> findAllByOrderByIdAsc();

    List<PaymentEntity> findByContract_ContractNumberOrderByIdAsc(String contractNumber);
}
