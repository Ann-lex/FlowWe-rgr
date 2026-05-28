package com.example.FloWe.controller;

import com.example.FloWe.model.Category;
import com.example.FloWe.model.Product;
import com.example.FloWe.service.CategoryService;
import com.example.FloWe.service.ProductService;
import com.example.FloWe.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    public ProductController(ProductService productService,
                             CategoryService categoryService,
                             ReviewService reviewService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;
    }

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            Model model
    ) {
        model.addAttribute("categories", categoryService.findAll());

        if (categoryId == null) {
            model.addAttribute("showCategoriesOnly", true);
            return "products";
        }

        Optional<Category> selectedCategory = categoryService.findById(categoryId);

        if (selectedCategory.isEmpty()) {
            model.addAttribute("showCategoriesOnly", true);
            model.addAttribute("error", "Категория не найдена");
            return "products";
        }

        try {
            List<Product> products = productService.search(keyword, categoryId, minPrice, maxPrice);

            model.addAttribute("showCategoriesOnly", false);
            model.addAttribute("products", products);
            model.addAttribute("selectedCategory", selectedCategory.get());

            model.addAttribute("keyword", keyword);
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);

            return "products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("showCategoriesOnly", false);
            model.addAttribute("products", productService.search(null, categoryId, null, null));
            model.addAttribute("selectedCategory", selectedCategory.get());
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("error", e.getMessage());

            return "products";
        }
    }

    @GetMapping("/products/{id}")
    public String productDetails(
            @PathVariable Long id,
            @RequestParam(required = false) Long categoryId,
            Model model
    ) {
        Optional<Product> product = productService.findById(id);

        if (product.isEmpty()) {
            model.addAttribute("error", "Товар не найден");
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("showCategoriesOnly", true);
            return "products";
        }

        model.addAttribute("product", product.get());
        model.addAttribute("returnCategoryId", categoryId);

        model.addAttribute("reviews", reviewService.findByProductId(id));
        model.addAttribute("averageRating", reviewService.getAverageRating(id));
        model.addAttribute("reviewCount", reviewService.countReviews(id));

        return "product-details";
    }
}