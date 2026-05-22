package com.example.FloWe.controller;

import com.example.FloWe.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public String orders(Model model, Principal principal) {
        model.addAttribute("orders", orderService.findByUserEmail(principal.getName()));
        return "orders";
    }
}
