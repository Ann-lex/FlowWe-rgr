package com.example.FloWe.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Подтверждение регистрации FloWe");
        message.setText(
                "Здравствуйте!\n\n" +
                "Для подтверждения аккаунта перейдите по ссылке:\n" +
                verificationLink + "\n\n" +
                "Если вы не регистрировались в FloWe, просто проигнорируйте это письмо."
        );

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Восстановление пароля FloWe");
        message.setText(
                "Здравствуйте!\n\n" +
                "Для восстановления пароля перейдите по ссылке:\n" +
                resetLink + "\n\n" +
                "Если вы не запрашивали восстановление пароля, просто проигнорируйте это письмо."
        );

        mailSender.send(message);
    }
}