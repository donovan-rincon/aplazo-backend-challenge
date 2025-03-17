package com.aplazo.bnpl.repository;

import com.aplazo.bnpl.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Integer> {
}
