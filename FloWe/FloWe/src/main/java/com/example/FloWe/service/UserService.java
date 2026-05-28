package com.example.FloWe.service;

import com.example.FloWe.dto.RegisterRequest;
import com.example.FloWe.model.PasswordResetToken;
import com.example.FloWe.model.User;
import com.example.FloWe.model.VerificationToken;
import com.example.FloWe.repository.PasswordResetTokenRepository;
import com.example.FloWe.repository.UserRepository;
import com.example.FloWe.repository.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private static final Set<String> ROLES = Set.of("ADMIN", "SELLER", "BUYER");

    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$";
    private static final String PASSWORD_ERROR =
            "Пароль должен содержать латинские буквы и цифры, минимум 6 символов";

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserRepository userRepository,
                       VerificationTokenRepository verificationTokenRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {

        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        validatePassword(request.getPassword());

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("BUYER");
        user.setEnabled(false);
        user.setBalance(BigDecimal.ZERO);

        userRepository.save(user);

        User savedUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден после сохранения"));

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUserId(savedUser.getId());
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        verificationTokenRepository.save(verificationToken);

        String verificationLink = "http://localhost:8080/verify?token=" + token;

        emailService.sendVerificationEmail(savedUser.getEmail(), verificationLink);
    }

    public void verifyUser(String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Неверная ссылка подтверждения"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Срок действия ссылки истёк");
        }

        userRepository.enableUser(verificationToken.getUserId());
        verificationTokenRepository.deleteByToken(token);
    }

    public void resendVerificationEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким email не найден"));

        if (user.isEnabled()) {
            throw new IllegalArgumentException("Этот аккаунт уже подтверждён");
        }

        verificationTokenRepository.deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUserId(user.getId());
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        verificationTokenRepository.save(verificationToken);

        String verificationLink = "http://localhost:8080/verify?token=" + token;

        emailService.sendVerificationEmail(user.getEmail(), verificationLink);
    }

    public void createPasswordResetToken(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким email не найден"));

        passwordResetTokenRepository.deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUserId(user.getId());
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        passwordResetTokenRepository.save(passwordResetToken);

        String resetLink = "http://localhost:8080/reset-password?token=" + token;

        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    public void resetPassword(String token, String newPassword) {

        validatePassword(newPassword);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Неверная ссылка восстановления"));

        if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Срок действия ссылки истёк");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);

        userRepository.updatePassword(passwordResetToken.getUserId(), encodedPassword);
        passwordResetTokenRepository.deleteByToken(token);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким email не найден"));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void updateRole(Long userId, String role) {
        if (userId == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        if (role == null || !ROLES.contains(role)) {
            throw new IllegalArgumentException("Некорректная роль пользователя");
        }

        userRepository.updateRole(userId, role);
    }

    public void updateEnabled(Long userId, boolean enabled) {
        if (userId == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        userRepository.updateEnabled(userId, enabled);
    }

    public void updateBalance(Long userId, BigDecimal balance) {
        if (userId == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Баланс не может быть отрицательным");
        }

        userRepository.updateBalance(userId, balance);
    }

    private void validatePassword(String password) {
        if (password == null || !password.matches(PASSWORD_PATTERN)) {
            throw new IllegalArgumentException(PASSWORD_ERROR);
        }
    }
}