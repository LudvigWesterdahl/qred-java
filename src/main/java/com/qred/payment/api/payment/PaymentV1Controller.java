package com.qred.payment.api.payment;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/payment")
@RequiredArgsConstructor
public class PaymentV1Controller {

    private final PaymentV1Service service;

    @GetMapping
    public ResponseEntity<List<PaymentV1Response>> list(@RequestParam(required = false) String contractNumber) {
        final List<PaymentV1Response> response = service.list(contractNumber);
        return ResponseEntity.ok(response);
    }
}
