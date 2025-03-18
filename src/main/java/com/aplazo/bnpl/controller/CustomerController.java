package com.aplazo.bnpl.controller;

import com.aplazo.bnpl.config.JWTTokenUtil;
import com.aplazo.bnpl.model.dto.CustomerRequest;

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

import com.aplazo.bnpl.model.dto.CustomerResponse;

import com.aplazo.bnpl.model.entity.Customer;
import com.aplazo.bnpl.model.entity.User;
import com.aplazo.bnpl.repository.CustomerRepository;
import com.aplazo.bnpl.repository.UserRepository;
import com.aplazo.bnpl.service.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1")
public class CustomerController {

    @Autowired
    private JWTTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/customers")
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest customerRequest,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7); // Remove "Bearer "
        String username = jwtTokenUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));

        // Delegate customer creation logic to the service layer
        CustomerResponse response = customerService.createCustomer(customerRequest);

        Customer customer = customerRepository.findById(response.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Customer not found"));

        user.setCustomer(customer);
        userRepository.save(user);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable int customerId, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7); // Remove "Bearer "
        String username = jwtTokenUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));

        Customer customer = user.getCustomer();
        if (customer == null || customer.getId() != customerId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        CustomerResponse response = customerService.getCustomerById(customer.getId());
        return ResponseEntity.ok(response);
    }
}
