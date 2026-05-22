package com.example.FloWe.controller;

import com.example.FloWe.service.OrderService;
import com.example.FloWe.service.ProductService;
import com.example.FloWe.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;

    public AdminController(UserService userService,
                           ProductService productService,
                           OrderService orderService) {
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("products", productService.findAll());
        model.addAttribute("orders", orderService.findAll());

        return "admin";
    }

    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable Long id,
                                 @RequestParam String role) {
        userService.updateRole(id, role);
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/enabled")
    public String updateUserEnabled(@PathVariable Long id,
                                    @RequestParam(defaultValue = "false") boolean enabled) {
        userService.updateEnabled(id, enabled);
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/balance")
    public String updateUserBalance(@PathVariable Long id,
                                    @RequestParam BigDecimal balance) {
        userService.updateBalance(id, balance);
        return "redirect:/admin";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductByAdmin(id);
        return "redirect:/admin";
    }
}