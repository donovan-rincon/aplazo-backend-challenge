package com.aplazo.bnpl.controller;

import com.aplazo.bnpl.model.dto.CustomerRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aplazo.bnpl.model.dto.CustomerResponse;
import com.aplazo.bnpl.service.CustomerService;

@RestController
@RequestMapping("/v1")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/customers")
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest clientRequest) {
        // Delegate customer creation logic to the service layer
        CustomerResponse response = customerService.createClient(clientRequest);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CustomerResponse> getClientById(@PathVariable int customerId) {
        // Delegate customer retrieval logic to the service layer
        CustomerResponse response = customerService.getClientById(customerId);
        return ResponseEntity.ok(response);
    }
}
