package com.example.FloWe.controller;

import com.example.FloWe.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PasswordResetController {

    private final UserService userService;

    public PasswordResetController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        try {
            userService.createPasswordResetToken(email);

            model.addAttribute(
                    "message",
                    "Если пользователь с таким email существует, ссылка для восстановления отправлена на почту."
            );

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());

        } catch (Exception e) {
            model.addAttribute(
                    "error",
                    "Не удалось отправить письмо для восстановления пароля. Проверьте настройки почты или попробуйте позже."
            );
        }

        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                       @RequestParam String password,
                                       Model model) {
        try {
            userService.resetPassword(token, password);

            model.addAttribute(
                    "message",
                    "Пароль успешно изменён. Теперь вы можете войти в аккаунт."
            );

            return "login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);

            return "reset-password";

        } catch (Exception e) {
            model.addAttribute("error", "Не удалось изменить пароль. Попробуйте позже.");
            model.addAttribute("token", token);

            return "reset-password";
        }
    }
}