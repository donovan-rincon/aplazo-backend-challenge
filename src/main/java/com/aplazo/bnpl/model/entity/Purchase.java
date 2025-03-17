package com.aplazo.bnpl.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use auto-increment for integers
    @Column(updatable = false, nullable = false)
    private int id;
}
