package com.example.FloWe.service;

import com.example.FloWe.model.Payment;
import com.example.FloWe.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public void createSuccessfulPayment(Long orderId, Long userId, BigDecimal amount) {
        if (orderId == null) {
            throw new IllegalArgumentException("Заказ не найден");
        }

        if (userId == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма оплаты должна быть больше нуля");
        }

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setUserId(userId);
        payment.setAmount(amount);
        payment.setPaymentMethod("BALANCE");
        payment.setStatus("PAID");

        paymentRepository.save(payment);
    }

    public List<Payment> findByUserId(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }
}