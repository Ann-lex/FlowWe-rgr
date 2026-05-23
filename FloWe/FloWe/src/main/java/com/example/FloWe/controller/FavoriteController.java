package com.example.FloWe.controller;

import com.example.FloWe.service.FavoriteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public String favorites(Authentication authentication, Model model) {
        String email = authentication.getName();

        model.addAttribute("favorites", favoriteService.findByUserEmail(email));

        return "favorites";
    }

    @PostMapping("/add/{productId}")
    public String add(@PathVariable Long productId,
                      Authentication authentication) {

        favoriteService.add(authentication.getName(), productId);

        return "redirect:/favorites";
    }

    @PostMapping("/remove/{productId}")
    public String remove(@PathVariable Long productId,
                         Authentication authentication) {

        favoriteService.remove(authentication.getName(), productId);

        return "redirect:/favorites";
    }
}