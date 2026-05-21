package com.example.FloWe.controller;

import com.example.FloWe.model.User;
import com.example.FloWe.service.ProductService;
import com.example.FloWe.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class SellerController {

    private final UserService userService;
    private final ProductService productService;

    public SellerController(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    @GetMapping("/seller")
    public String sellerPage(Model model, Principal principal) {
        User currentSeller = getCurrentSeller(principal);

        int productCount = productService.findBySellerId(currentSeller.getId()).size();

        model.addAttribute("sellerName", getSellerName(currentSeller));
        model.addAttribute("productCount", productCount);

        return "seller";
    }

    private User getCurrentSeller(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Пользователь не авторизован");
        }

        return userService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    private String getSellerName(User user) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        if (firstName == null || firstName.isBlank()) {
            return user.getEmail();
        }

        if (lastName == null || lastName.isBlank()) {
            return firstName;
        }

        return firstName + " " + lastName;
    }
}