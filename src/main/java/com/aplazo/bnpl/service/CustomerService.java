package com.aplazo.bnpl.service;

import com.aplazo.bnpl.model.dto.CustomerRequest;
import com.aplazo.bnpl.model.dto.CustomerResponse;
import com.aplazo.bnpl.model.entity.Customer;
import com.aplazo.bnpl.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;

@Service
public class CustomerService {
    private final CustomerRepository clientRepository;

    public CustomerService(CustomerRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public static int getAge(String birthDateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate birthDate = LocalDate.parse(birthDateString, formatter);
            return Period.between(birthDate, LocalDate.now()).getYears();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd");
        }
    }

    public CustomerResponse createCustomer(CustomerRequest customerRequest) {
        Customer customer = new Customer();
        customer.setFirstName(customerRequest.getFirstName());
        customer.setLastName(customerRequest.getLastName());
        customer.setSecondLastName(customerRequest.getSecondLastName());
        customer.setDateOfBirth(customerRequest.getDateOfBirth().toString());

        /*
         * - $3,000 for clients aged 18 to 25 years.
         * - $5,000 for clients aged 26 to 30 years.
         * - $8,000 for clients aged 31 to 65 years.
         * - Clients under 18 or over 65 are not accepted.
         */
        int age = getAge(customerRequest.getDateOfBirth());
        double creditLine = 0.0;
        if (age < 18 || age > 65) {
            return null;
        } else if (age >= 18 && age <= 25) {
            creditLine = 3000.0;
        } else if (age >= 26 && age <= 30) {
            creditLine = 5000.0;
        } else if (age >= 31 && age <= 65) {
            creditLine = 8000.0;
        }
        customer.setCreditLine(creditLine);
        customer.setCreditUsed(0.0);
        customer.setCreatedAt(LocalDateTime.now());
        Customer savedCustomer = clientRepository.save(customer);

        CustomerResponse response = new CustomerResponse();
        response.setCustomerId(savedCustomer.getId());
        response.setCreditLineAmount(savedCustomer.getCreditLine());
        response.setAvailableCreditLineAmount(savedCustomer.getCreditLine() - savedCustomer.getCreditUsed());
        response.setCreatedAt(savedCustomer.getCreatedAt());
        return response;
    }

    public CustomerResponse getCustomerById(int customerId) {
        Customer customer = clientRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        CustomerResponse response = new CustomerResponse();
        response.setCustomerId(customer.getId());
        response.setCreditLineAmount(customer.getCreditLine());
        response.setAvailableCreditLineAmount(customer.getCreditLine() - customer.getCreditUsed());
        response.setCreatedAt(customer.getCreatedAt());

        return response;
    }
}
