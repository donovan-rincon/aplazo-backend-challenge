package com.aplazo.bnpl.model.entity;

import java.time.LocalDateTime;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use auto-increment for integers
    @Column(updatable = false, nullable = false)
    private int id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String secondLastName;

    @Column(nullable = false)
    private String dateOfBirth;

    @Column(nullable = false)
    private double creditLine;

    @Column(nullable = false)
    private double creditUsed;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
