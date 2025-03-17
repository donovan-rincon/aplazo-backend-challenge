package com.aplazo.bnpl.model.dto;

import jakarta.validation.constraints.NotNull;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanRequest {

    @NotNull
    private int customerId;

    @NotNull
    private double amount;

}
