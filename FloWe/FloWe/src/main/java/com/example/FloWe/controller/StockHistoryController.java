package com.example.FloWe.controller;

import com.example.FloWe.model.User;
import com.example.FloWe.service.StockHistoryService;
import com.example.FloWe.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class StockHistoryController {

    private final StockHistoryService stockHistoryService;
    private final UserService userService;

    public StockHistoryController(StockHistoryService stockHistoryService,
                                  UserService userService) {
        this.stockHistoryService = stockHistoryService;
        this.userService = userService;
    }

    @GetMapping("/admin/stock-history")
    public String adminStockHistory(Model model) {
        model.addAttribute("history", stockHistoryService.findAll());
        return "admin-stock-history";
    }

    @GetMapping("/seller/stock-history")
    public String sellerStockHistory(Model model, Principal principal) {
        User seller = userService.findByEmail(principal.getName());
        model.addAttribute("history", stockHistoryService.findBySellerId(seller.getId()));
        return "seller-stock-history";
    }
}