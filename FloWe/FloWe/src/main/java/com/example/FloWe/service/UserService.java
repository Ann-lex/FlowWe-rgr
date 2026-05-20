package com.example.FloWe.service;

import com.example.FloWe.dto.RegisterRequest;
import com.example.FloWe.model.User;
import com.example.FloWe.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole("BUYER");
        user.setEnabled(false);
        user.setBalance(BigDecimal.ZERO);

        userRepository.save(user);
    }
}