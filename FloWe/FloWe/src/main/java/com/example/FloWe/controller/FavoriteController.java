package com.example.FloWe.controller;

import com.example.FloWe.service.FavoriteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public String favorites(Model model, Principal principal) {
        model.addAttribute("favorites", favoriteService.findByUserEmail(principal.getName()));
        return "favorites";
    }

    @PostMapping("/items")
    public String add(@RequestParam Long productId,
                      @RequestParam(defaultValue = "PRIVATE") String accessLevel,
                      Principal principal) {
        favoriteService.add(principal.getName(), productId, accessLevel);
        return "redirect:/favorites";
    }

    @PostMapping("/items/{productId}/access")
    public String updateAccess(@PathVariable Long productId,
                               @RequestParam String accessLevel,
                               Principal principal) {
        favoriteService.updateAccessLevel(principal.getName(), productId, accessLevel);
        return "redirect:/favorites";
    }

    @PostMapping("/items/{productId}/delete")
    public String delete(@PathVariable Long productId, Principal principal) {
        favoriteService.delete(principal.getName(), productId);
        return "redirect:/favorites";
    }
}
