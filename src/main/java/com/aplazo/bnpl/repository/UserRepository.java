package com.aplazo.bnpl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aplazo.bnpl.model.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username); // Used for authentication
}
