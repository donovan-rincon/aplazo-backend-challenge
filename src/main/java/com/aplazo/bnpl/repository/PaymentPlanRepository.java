
package com.aplazo.bnpl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aplazo.bnpl.model.entity.PaymentPlan;

public interface PaymentPlanRepository extends JpaRepository<PaymentPlan, Integer> {
    PaymentPlan findByName(String name);
}
