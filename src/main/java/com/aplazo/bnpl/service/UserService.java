package com.aplazo.bnpl.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aplazo.bnpl.model.dto.UserRequest;
import com.aplazo.bnpl.model.entity.User;
import com.aplazo.bnpl.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(UserRequest userRequest) {
        // Create and save the User
        User user = User.builder()
                .username(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .createdAt(LocalDateTime.now()) // Encrypt the password
                .build();
        userRepository.save(user);
    }
}
