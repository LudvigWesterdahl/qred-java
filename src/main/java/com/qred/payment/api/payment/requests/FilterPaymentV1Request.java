package com.qred.payment.api.payment.requests;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "FilterPaymentV1Request")
@Setter
@Getter
public final class FilterPaymentV1Request {

    @Parameter(
            name = "contract_number",
            schema = @Schema(type = "string"),
            required = true
    )
    @NotNull
    private String contract_number;
}

