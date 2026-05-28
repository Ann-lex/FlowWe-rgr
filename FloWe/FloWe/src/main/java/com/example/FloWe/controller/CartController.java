package com.example.FloWe.controller;

import com.example.FloWe.service.CartService;
import com.example.FloWe.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                          Principal principal,
                          RedirectAttributes redirectAttributes) {
        try {
            cartService.addItem(principal.getName(), productId, quantity);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Не удалось добавить товар в корзину");
        }

        return "redirect:/cart";
    }

    @PostMapping("/items/{itemId}/quantity")
    public String updateQuantity(@PathVariable Long itemId,
                                 @RequestParam Integer quantity,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        try {
            cartService.updateQuantity(principal.getName(), itemId, quantity);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Не удалось обновить количество товара");
        }

        return "redirect:/cart";
    }

    @PostMapping("/items/{itemId}/delete")
    public String deleteItem(@PathVariable Long itemId,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        try {
            cartService.deleteItem(principal.getName(), itemId);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Не удалось удалить товар из корзины");
        }

        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clear(Principal principal,
                        RedirectAttributes redirectAttributes) {
        try {
            cartService.clear(principal.getName());
            redirectAttributes.addFlashAttribute("success", "Корзина очищена");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Не удалось очистить корзину");
        }

        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(Principal principal,
                           RedirectAttributes redirectAttributes) {
        try {
            orderService.checkout(principal.getName());
            redirectAttributes.addFlashAttribute("success", "Заказ успешно оформлен");
            return "redirect:/orders";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Не удалось оформить заказ");
            return "redirect:/cart";
        }
    }
}