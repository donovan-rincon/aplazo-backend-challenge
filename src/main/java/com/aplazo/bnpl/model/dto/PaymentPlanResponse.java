package com.aplazo.bnpl.model.dto;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentPlanResponse {
    private Double commissionAmount;
    private List<InstallmentResponse> installments;
}
