package com.aplazo.bnpl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.aplazo.bnpl.config.JWTTokenUtil;
import com.aplazo.bnpl.model.dto.LoanRequest;

import com.aplazo.bnpl.model.dto.LoanResponse;
import com.aplazo.bnpl.model.entity.Customer;
import com.aplazo.bnpl.model.entity.Loan;
import com.aplazo.bnpl.model.entity.User;
import com.aplazo.bnpl.repository.LoanRepository;
import com.aplazo.bnpl.repository.UserRepository;
import com.aplazo.bnpl.service.LoanService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1")
public class LoanController {

    @Autowired
    private JWTTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanRepository loanRepository;

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/loans")
    public ResponseEntity<LoanResponse> createCustomer(@RequestBody LoanRequest loanRequest,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7); // Remove "Bearer "
        String username = jwtTokenUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));

        Customer customer = user.getCustomer();
        if (customer == null || customer.getId() != loanRequest.getCustomerId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        // Delegate loan creation logic to the service layer
        LoanResponse response = loanService.createLoan(loanRequest);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/loans/{loanId}")
    public ResponseEntity<LoanResponse> getClientById(@PathVariable int loanId, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7); // Remove "Bearer "
        String username = jwtTokenUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));

        Customer customer = user.getCustomer();
        if (customer == null || customer.getId() != loan.getCustomerId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        // Delegate loan retrieval logic to the service layer
        LoanResponse response = loanService.getLoanById(loan.getId());
        return ResponseEntity.ok(response);
    }
}
