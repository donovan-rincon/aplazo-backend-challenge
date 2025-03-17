package com.aplazo.bnpl.service;

import com.aplazo.bnpl.model.dto.LoanRequest;
import com.aplazo.bnpl.model.dto.LoanResponse;

import com.aplazo.bnpl.model.dto.PaymentPlanResponse;
import com.aplazo.bnpl.model.dto.InstallmentResponse;
import com.aplazo.bnpl.model.entity.Customer;
import com.aplazo.bnpl.model.entity.Installment;
import com.aplazo.bnpl.model.entity.Loan;
import com.aplazo.bnpl.model.entity.PaymentPlan;
import com.aplazo.bnpl.model.enums.LoanStatus;
import com.aplazo.bnpl.repository.ClientRepository;
import com.aplazo.bnpl.repository.LoanRepository;
import com.aplazo.bnpl.repository.PaymentPlanRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final ClientRepository clientRepository;
    private final PaymentPlanRepository paymentPlanRepository;

    public LoanService(LoanRepository loanRepository, ClientRepository clientRepository,
            PaymentPlanRepository paymentPlanRepository) {
        this.loanRepository = loanRepository;
        this.clientRepository = clientRepository;
        this.paymentPlanRepository = paymentPlanRepository;
    }

    private PaymentPlan determinePaymentPlan(Customer customer) {
        String paymentPlanName = "Scheme 1"; // Example: Always select Scheme 1
        return paymentPlanRepository.findByName(paymentPlanName); // Example: Always select Scheme 1

    }

    private List<Installment> generateInstallments(Loan loan) {
        List<Installment> installments = new ArrayList<>();
        PaymentPlan paymentPlan = loan.getPaymentPlan();
        Double totalAmount = loan.getAmount();
        Double interestRate = paymentPlan.getInterestRate() / 100;
        Double amountWithInterest = totalAmount * (1 + interestRate);
        Double installmentAmount = amountWithInterest / paymentPlan.getNumberOfPayments();

        int paymentCadence = paymentPlan.getFrequency() == "Biweekly" ? 2 : 1; // TODO: enum for payemnt cadences

        // Generate installments
        LocalDate startDate = LocalDate.now();
        for (int i = 0; i < paymentPlan.getNumberOfPayments(); i++) {
            LocalDate paymentDate = startDate.plusWeeks(paymentCadence * i);
            Installment installment = Installment.builder()
                    .amount(installmentAmount)
                    .scheduledPaymentDate(paymentDate)
                    .status("NEXT") // Default status
                    .loan(loan)
                    .build();
            installments.add(installment);
        }

        return installments;
    }

    public LoanResponse createLoan(LoanRequest loanRequest) {
        Customer customer = clientRepository.findById(loanRequest.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        PaymentPlan paymentPlan = determinePaymentPlan(customer);
        Loan loan = Loan.builder()
                .customerId(customer.getId())
                .amount(loanRequest.getAmount())
                .status(LoanStatus.ACTIVE.toString())
                .createdAt(LocalDateTime.now())
                .paymentPlan(paymentPlan)
                .build();

        // Generate installments
        List<Installment> installments = generateInstallments(loan);
        loan.setInstallments(installments);

        Loan savedLoan = loanRepository.save(loan);

        LoanResponse response = LoanResponse.builder()
                .id(savedLoan.getId())
                .customerId(savedLoan.getCustomerId())
                .amount(savedLoan.getAmount())
                .status(savedLoan.getStatus())
                .paymentPlan(PaymentPlanResponse.builder()
                        .commissionAmount(savedLoan.getAmount() * (1 + savedLoan.getPaymentPlan().getInterestRate()))
                        .installments(savedLoan.getInstallments().stream()
                                .map(installment -> InstallmentResponse.builder()
                                        .amount(installment.getAmount())
                                        .scheduledPaymentDate(installment.getScheduledPaymentDate().toString())
                                        .status(installment.getStatus())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .build();

        return response;
    }

    public LoanResponse getLoanById(int loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        LoanResponse response = LoanResponse.builder()
                .id(loan.getId())
                .customerId(loan.getCustomerId())
                .amount(loan.getAmount())
                .status(loan.getStatus())
                .paymentPlan(PaymentPlanResponse.builder()
                        .commissionAmount(loan.getAmount() * (1 + loan.getPaymentPlan().getInterestRate()))
                        .installments(loan.getInstallments().stream()
                                .map(installment -> InstallmentResponse.builder()
                                        .amount(installment.getAmount())
                                        .scheduledPaymentDate(installment.getScheduledPaymentDate().toString())
                                        .status(installment.getStatus())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .build();

        return response;
    }
}
