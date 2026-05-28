package com.example.FloWe.controller;

import com.example.FloWe.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/admin/payments")
    public String adminPayments(Model model) {
        model.addAttribute("payments", paymentService.findAll());

        return "admin-payments";
    }
}