package com.example.FloWe.service;

import com.example.FloWe.dto.RegisterRequest;
import com.example.FloWe.model.User;
import com.example.FloWe.model.VerificationToken;
import com.example.FloWe.repository.UserRepository;
import com.example.FloWe.repository.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserRepository userRepository,
                       VerificationTokenRepository verificationTokenRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {

        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Пользователь с таким email уже существует"
            );
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole("BUYER");
        user.setEnabled(false);
        user.setBalance(BigDecimal.ZERO);

        userRepository.save(user);

        User savedUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Пользователь не найден после сохранения")
                );

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();

        verificationToken.setToken(token);
        verificationToken.setUserId(savedUser.getId());
        verificationToken.setExpiryDate(
                LocalDateTime.now().plusHours(24)
        );

        verificationTokenRepository.save(verificationToken);

        String verificationLink =
                "http://localhost:8080/verify?token=" + token;

        emailService.sendVerificationEmail(
                savedUser.getEmail(),
                verificationLink
        );
    }

    public void verifyUser(String token) {

        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Неверная ссылка подтверждения"
                                )
                        );

        if (verificationToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "Срок действия ссылки истёк"
            );
        }

        userRepository.enableUser(
                verificationToken.getUserId()
        );

        verificationTokenRepository.deleteByToken(token);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}