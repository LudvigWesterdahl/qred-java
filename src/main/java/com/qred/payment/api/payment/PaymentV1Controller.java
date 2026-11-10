package com.qred.payment.api.payment;

import com.qred.payment.api.payment.requests.CreatePaymentV1Request;
import com.qred.payment.api.payment.requests.FilterPaymentV1Request;
import com.qred.payment.api.payment.responses.PaymentV1Response;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import lombok.RequiredArgsConstructor;

// TODO: Create a GET to return a single payment (and include the URL in the Location header for the POST).
// TODO: Document the swagger, include explanation on the top, versioning, examples and proper handling of enum types.
@RestController
@RequestMapping("v1/payment")
@RequiredArgsConstructor
public class PaymentV1Controller {

    private final PaymentV1Service service;

    // NOTE: The objective said to query on contract, but the approach said to filter on client. For simplicity, I just implemented the contract.
    // Because the input data contains the contract and from the current API, client is sort of in the background and not in the inputs.
    @GetMapping
    public ResponseEntity<List<PaymentV1Response>> getPayments(@Valid @ParameterObject final FilterPaymentV1Request request) {
        final List<PaymentV1Response> response = service.getPayments(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> createPayment(@Valid @RequestBody final CreatePaymentV1Request request) {
        service.createPayment(request);

        return ResponseEntity.noContent().build();
    }
}
