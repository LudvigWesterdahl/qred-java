package com.qred.payment.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    /**
     * Supported by index {@code uq_client_client_number}.
     */
    Optional<ClientEntity> findByClientNumber(String clientNumber);
}
