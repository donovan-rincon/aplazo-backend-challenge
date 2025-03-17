package com.aplazo.bnpl.repository;

import com.aplazo.bnpl.model.entity.Customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Customer, Integer> {
}
