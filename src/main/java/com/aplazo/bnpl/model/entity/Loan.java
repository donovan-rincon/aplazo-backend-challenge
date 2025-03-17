package com.aplazo.bnpl.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private int customerId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String status; // Enum for LoanStatus (e.g., ACTIVE, LATE)

    @ManyToOne
    @JoinColumn(name = "payment_plan_id", nullable = false)
    private PaymentPlan paymentPlan; // Associated payment plan

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Installment> installments; // List of generated installments

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
