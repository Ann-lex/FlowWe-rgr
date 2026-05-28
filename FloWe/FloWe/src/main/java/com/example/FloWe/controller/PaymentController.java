package com.example.FloWe.controller;

import com.example.FloWe.model.User;
import com.example.FloWe.service.PaymentService;
import com.example.FloWe.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    public PaymentController(PaymentService paymentService, UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @GetMapping("/profile/payments")
    public String userPayments(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());

        model.addAttribute("payments", paymentService.findByUserId(user.getId()));

        return "profile-payments";
    }

    @GetMapping("/admin/payments")
    public String adminPayments(Model model) {
        model.addAttribute("payments", paymentService.findAll());

        return "admin-payments";
    }
}