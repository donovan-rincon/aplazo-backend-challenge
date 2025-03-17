package com.aplazo.bnpl.model.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private int customerId;
    private double creditLineAmount;
    private double availableCreditLineAmount;
    private LocalDateTime createdAt;
}
