package com.example.FloWe.controller;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import com.example.FloWe.dto.RegisterRequest;
import com.example.FloWe.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String register(@Valid RegisterRequest registerRequest,
                           BindingResult bindingResult,
                           Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.register(registerRequest);

            model.addAttribute(
                    "message",
                    "Регистрация прошла успешно. Проверьте почту и перейдите по ссылке для подтверждения аккаунта."
            );

            return "register-success";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
    @GetMapping("/verify")
    public String verify(@RequestParam String token, Model model) {
        try {
            userService.verifyUser(token);
            model.addAttribute("message", "Аккаунт успешно подтвержден");
            return "verification-success";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "verification-error";
        }
    }
}