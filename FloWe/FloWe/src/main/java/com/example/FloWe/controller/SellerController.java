package com.example.FloWe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SellerController {

    @GetMapping("/seller")
    public String sellerPage() {
        return "seller";
    }
}