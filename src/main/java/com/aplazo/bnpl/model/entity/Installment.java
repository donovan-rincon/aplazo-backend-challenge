package com.aplazo.bnpl.model.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Installment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double amount; // Installment amount

    private LocalDate scheduledPaymentDate; // Scheduled date for payment

    private String status; // Enum for InstallmentStatus (NEXT, PENDING, ERROR)

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;
}
