package com.example.FloWe.controller;

import com.example.FloWe.dto.RegisterRequest;
import com.example.FloWe.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(RegisterRequest registerRequest, Model model) {
        try {
            userService.register(registerRequest);
            model.addAttribute("message", "Регистрация прошла успешно. Проверьте почту для подтверждения аккаунта.");
            return "register-success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}