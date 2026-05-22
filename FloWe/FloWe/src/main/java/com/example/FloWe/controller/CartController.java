package com.example.FloWe.controller;

import com.example.FloWe.service.CartService;
import com.example.FloWe.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;

    public CartController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping
    public String cart(Model model, Principal principal) {
        model.addAttribute("cart", cartService.getCart(principal.getName()));
        return "cart";
    }

    @PostMapping("/items")
    public String addItem(@RequestParam Long productId,
                          @RequestParam(required = false) Integer quantity,
                          Principal principal) {
        cartService.addItem(principal.getName(), productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/items/{itemId}/quantity")
    public String updateQuantity(@PathVariable Long itemId,
                                 @RequestParam Integer quantity,
                                 Principal principal) {
        cartService.updateQuantity(principal.getName(), itemId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/items/{itemId}/delete")
    public String deleteItem(@PathVariable Long itemId, Principal principal) {
        cartService.deleteItem(principal.getName(), itemId);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clear(Principal principal) {
        cartService.clear(principal.getName());
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(Principal principal) {
        orderService.checkout(principal.getName());
        return "redirect:/orders";
    }
}