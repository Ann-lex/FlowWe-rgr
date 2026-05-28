package com.example.FloWe.controller;

import com.example.FloWe.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/products/{productId}/reviews")
    public String addReview(@PathVariable Long productId,
                            @RequestParam Integer rating,
                            @RequestParam String text,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        try {
            reviewService.addReview(productId, principal.getName(), rating, text);
            redirectAttributes.addFlashAttribute("reviewSuccess", "Отзыв успешно добавлен");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("reviewError", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("reviewError", "Не удалось добавить отзыв");
        }

        return "redirect:/products/" + productId;
    }
}