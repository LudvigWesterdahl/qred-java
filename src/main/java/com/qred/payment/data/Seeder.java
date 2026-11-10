package com.qred.payment.data;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
final class Seeder {

    private static final Logger log = LoggerFactory.getLogger(Seeder.class);

    private static final Map<String, String> CONTRACT_NUMBER_TO_CLIENT_NUMBER = Map.of(
            "101", "1",
            "102", "1",
            "103", "1",
            "201", "2",
            "301", "3",
            "302", "3",
            "401", "4"
    );

    private final ClientRepository clientRepository;
    private final ContractRepository contractRepository;
    private final TransactionHelper transactionHelper;

    @PostConstruct
    void execute() {
        log.info("-".repeat(25));
        log.info("seeding db");
        log.info("-".repeat(25));

        transactionHelper.executeWithoutResult(transactionStatus -> {
            for (final Map.Entry<String, String> entry : CONTRACT_NUMBER_TO_CLIENT_NUMBER.entrySet()) {
                final String contractNumber = entry.getKey();
                final String clientNumber = entry.getValue();

                final ContractEntity currentContractEntity = contractRepository.findByContractNumber(contractNumber)
                        .orElse(null);
                if (currentContractEntity != null) {
                    continue;
                }

                ClientEntity clientEntity = clientRepository.findByClientNumber(clientNumber)
                        .orElse(null);
                if (clientEntity == null) {
                    clientEntity = new ClientEntity();
                    clientEntity.setClientNumber(clientNumber);
                    clientRepository.save(clientEntity);
                }

                final ContractEntity contractEntity = new ContractEntity();
                contractEntity.setClient(clientEntity);
                contractEntity.setContractNumber(contractNumber);

                contractRepository.save(contractEntity);
            }
        });
    }
}

