package com.example.FloWe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 20, message = "Имя должно быть от 2 до 20 символов")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 20, message = "Фамилия должна быть от 2 до 20 символов")
    private String lastName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Введите корректный email")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$",
            message = "Пароль должен содержать латинские буквы и цифры, минимум 6 символов"
    )
    private String password;

    public RegisterRequest() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}