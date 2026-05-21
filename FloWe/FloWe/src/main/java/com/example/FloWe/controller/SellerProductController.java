package com.example.FloWe.controller;

import com.example.FloWe.dto.ProductForm;
import com.example.FloWe.model.User;
import com.example.FloWe.service.CategoryService;
import com.example.FloWe.service.ProductService;
import com.example.FloWe.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class SellerProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;

    public SellerProductController(ProductService productService,
                                   CategoryService categoryService,
                                   UserService userService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping("/seller/products")
    public String sellerProducts(Model model, Principal principal) {
        User currentSeller = getCurrentSeller(principal);

        model.addAttribute("products", productService.findBySellerId(currentSeller.getId()));
        model.addAttribute("sellerEmail", currentSeller.getEmail());

        return "seller-products";
    }

    @GetMapping("/seller/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("formTitle", "Добавление товара");
        model.addAttribute("formAction", "/seller/products/add");

        return "product-form";
    }

    @PostMapping("/seller/products/add")
    public String addProduct(ProductForm productForm, Model model, Principal principal) {
        User currentSeller = getCurrentSeller(principal);

        try {
            productService.createProduct(productForm, currentSeller.getId());

            return "redirect:/seller/products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("formTitle", "Добавление товара");
            model.addAttribute("formAction", "/seller/products/add");
            model.addAttribute("productForm", productForm);

            return "product-form";
        }
    }

    @GetMapping("/seller/products/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model, Principal principal) {
        User currentSeller = getCurrentSeller(principal);

        try {
            ProductForm productForm = productService.getProductFormForEdit(id, currentSeller.getId());

            model.addAttribute("productForm", productForm);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("formTitle", "Редактирование товара");
            model.addAttribute("formAction", "/seller/products/edit/" + id);

            return "product-form";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("products", productService.findBySellerId(currentSeller.getId()));
            model.addAttribute("sellerEmail", currentSeller.getEmail());

            return "seller-products";
        }
    }

    @PostMapping("/seller/products/edit/{id}")
    public String editProduct(@PathVariable Long id,
                              ProductForm productForm,
                              Model model,
                              Principal principal) {
        User currentSeller = getCurrentSeller(principal);

        try {
            productForm.setId(id);
            productService.updateProduct(productForm, currentSeller.getId());

            return "redirect:/seller/products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("formTitle", "Редактирование товара");
            model.addAttribute("formAction", "/seller/products/edit/" + id);
            model.addAttribute("productForm", productForm);

            return "product-form";
        }
    }

    @PostMapping("/seller/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, Model model, Principal principal) {
        User currentSeller = getCurrentSeller(principal);

        try {
            productService.deleteProduct(id, currentSeller.getId());

            return "redirect:/seller/products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("products", productService.findBySellerId(currentSeller.getId()));
            model.addAttribute("sellerEmail", currentSeller.getEmail());

            return "seller-products";
        }
    }

    private User getCurrentSeller(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Пользователь не авторизован");
        }
        return userService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }
}