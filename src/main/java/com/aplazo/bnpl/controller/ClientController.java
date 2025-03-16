package com.aplazo.bnpl.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aplazo.bnpl.model.dto.ClientResponse;
import com.aplazo.bnpl.service.ClientService;

@RestController
@RequestMapping("/client")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody ClientRequest clientRequest) {
        // Delegate client creation logic to the service layer
        ClientResponse response = clientService.createClient(clientRequest);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable String customerId) {
        // Delegate client retrieval logic to the service layer
        ClientResponse response = clientService.getClientById(customerId);
        return ResponseEntity.ok(response);
    }
}
