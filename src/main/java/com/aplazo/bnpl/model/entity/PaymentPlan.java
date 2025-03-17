package com.aplazo.bnpl.model.entity;

import jakarta.persistence.*;

import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name; // Scheme 1, Scheme 2

    private Integer numberOfPayments; // e.g., 5

    private String frequency; // Biweekly, etc.

    private Double interestRate; // e.g., 13% or 16%
}
