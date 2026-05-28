package com.example.FloWe.controller;

import com.example.FloWe.dto.RegisterRequest;
import com.example.FloWe.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
                           BindingResult bindingResult,
                           Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            boolean emailSent = userService.register(registerRequest);

            if (emailSent) {
                model.addAttribute(
                        "message",
                        "Регистрация прошла успешно. Проверьте почту и перейдите по ссылке для подтверждения аккаунта."
                );
            } else {
                model.addAttribute(
                        "message",
                        "Аккаунт создан, но письмо подтверждения не удалось отправить. " +
                                "Попробуйте отправить письмо повторно на странице подтверждения email."
                );
            }

            model.addAttribute("email", registerRequest.getEmail());

            return "register-success";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";

        } catch (Exception e) {
            model.addAttribute(
                    "error",
                    "Не удалось завершить регистрацию. Проверьте введённые данные или попробуйте позже."
            );
            return "register";
        }
    }

    @GetMapping("/resend-verification")
    public String showResendVerificationForm() {
        return "resend-verification";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam String email, Model model) {
        try {
            userService.resendVerificationEmail(email);

            model.addAttribute(
                    "message",
                    "Новое письмо подтверждения отправлено на почту."
            );

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());

        } catch (Exception e) {
            model.addAttribute(
                    "error",
                    "Не удалось отправить письмо. Проверьте настройки почты или попробуйте позже."
            );
        }

        return "resend-verification";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam String token, Model model) {
        try {
            userService.verifyUser(token);
            model.addAttribute("message", "Аккаунт успешно подтверждён");
            return "verification-success";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "verification-error";

        } catch (Exception e) {
            model.addAttribute("error", "Не удалось подтвердить аккаунт. Попробуйте позже.");
            return "verification-error";
        }
    }
}