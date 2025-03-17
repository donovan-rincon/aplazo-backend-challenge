package com.aplazo.bnpl.model.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanResponse {

    private Integer id;
    private Integer customerId;
    private String status; // ACTIVE, LATE, COMPLETED
    private double amount;
    private LocalDateTime createdAt;
    private PaymentPlanResponse paymentPlan;
}
