package com.example.FloWe.controller;

import com.example.FloWe.model.Product;
import com.example.FloWe.service.CategoryService;
import com.example.FloWe.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            Model model
    ) {
        try {
            List<Product> products = productService.search(keyword, categoryId, minPrice, maxPrice);

            model.addAttribute("products", products);
            model.addAttribute("categories", categoryService.findAll());

            model.addAttribute("keyword", keyword);
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);

            return "products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("products", productService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("error", e.getMessage());

            return "products";
        }
    }
}