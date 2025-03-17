package com.aplazo.bnpl.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentResponse {

    private Double amount;
    private String scheduledPaymentDate;
    private String status; // NEXT, PENDING, ERROR

}
