package com.aplazo.bnpl.model.entity;

import java.time.LocalDateTime;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "application_user") // renamed to avoid conflicts sql reserved words
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use auto-increment for integers
    @Column(updatable = false, nullable = false)
    private int id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // Reference to Customer entity

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
