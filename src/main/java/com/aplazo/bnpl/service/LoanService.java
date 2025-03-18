package com.aplazo.bnpl.service;

import com.aplazo.bnpl.model.dto.LoanRequest;
import com.aplazo.bnpl.model.dto.LoanResponse;

import com.aplazo.bnpl.model.dto.PaymentPlanResponse;
import com.aplazo.bnpl.model.dto.InstallmentResponse;
import com.aplazo.bnpl.model.entity.*;
import com.aplazo.bnpl.model.enums.LoanStatus;
import com.aplazo.bnpl.repository.*;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository clientRepository;
    private final PaymentPlanRepository paymentPlanRepository;

    public LoanService(LoanRepository loanRepository, CustomerRepository clientRepository,
            PaymentPlanRepository paymentPlanRepository) {
        this.loanRepository = loanRepository;
        this.clientRepository = clientRepository;
        this.paymentPlanRepository = paymentPlanRepository;
    }

    private PaymentPlan determinePaymentPlan(Customer customer) {
        /*
         * - Assign Scheme 1 if the first name of the client starts with C, L, or H.
         * - Assign Scheme 2 if the client ID is greater than 25.
         * - Evaluate the rules in order, applying only the first applicable one.
         * - If no rule applies, Scheme 2 is assigned by default.
         */
        String paymentPlanName = "Scheme 2"; // Default scheme 2

        String regex = "^[CHL].*"; // Pattern without case-insensitive flag

        if (Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(customer.getFirstName()).matches()) {
            paymentPlanName = "Scheme 1";
        } else if (customer.getId() > 25) {
            paymentPlanName = "Scheme 2";
        }

        return paymentPlanRepository.findByName(paymentPlanName);
    }

    private List<Installment> generateInstallments(Loan loan) {
        List<Installment> installments = new ArrayList<>();
        PaymentPlan paymentPlan = loan.getPaymentPlan();
        Double totalAmount = loan.getAmount();
        Double interestRate = paymentPlan.getInterestRate() / 100;
        Double amountWithInterest = totalAmount * (1 + interestRate);
        Double installmentAmount = amountWithInterest / paymentPlan.getNumberOfPayments();

        int paymentCadence = paymentPlan.getFrequency() == "Biweekly" ? 2 : 1; // TODO: enum for payemnt cadence

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

        if (loanRequest.getAmount() > customer.getCreditLine() - customer.getCreditUsed()) {
            // TODO: return error response
            return null;
        }

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

        // Update customer credit used
        customer.setCreditUsed(loan.getAmount());
        clientRepository.save(customer);

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
