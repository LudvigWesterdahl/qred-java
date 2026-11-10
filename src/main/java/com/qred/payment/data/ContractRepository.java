package com.qred.payment.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<ContractEntity, Long> {

    /**
     * Supported by index {@code uq_contract_contract_number}.
     */
    Optional<ContractEntity> findByContractNumber(String contractNumber);
}
