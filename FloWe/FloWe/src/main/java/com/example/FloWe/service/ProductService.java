package com.example.FloWe.service;

import com.example.FloWe.dto.ProductForm;
import com.example.FloWe.model.Product;
import com.example.FloWe.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> search(String keyword, Long categoryId, String minPrice, String maxPrice) {
        BigDecimal min = parsePrice(minPrice);
        BigDecimal max = parsePrice(maxPrice);

        return productRepository.search(keyword, categoryId, min, max);
    }

    public void createProduct(ProductForm form) {
        validateProductForm(form);

        Product product = new Product();
        product.setName(form.getName().trim());
        product.setDescription(normalizeText(form.getDescription()));
        product.setPrice(form.getPrice());
        product.setQuantity(form.getQuantity());
        product.setImageUrl(normalizeText(form.getImageUrl()));

        /*
         * sellerId позже будет браться из авторизованного продавца.
         * Пока не трогаем Spring Security, чтобы не мешать участнику 1.
         */
        product.setSellerId(null);

        Long productId = productRepository.save(product);
        productRepository.addProductCategory(productId, form.getCategoryId());
    }

    public void updateProduct(ProductForm form) {
        if (form.getId() == null) {
            throw new IllegalArgumentException("Не указан id товара");
        }

        validateProductForm(form);

        Product product = new Product();
        product.setId(form.getId());
        product.setName(form.getName().trim());
        product.setDescription(normalizeText(form.getDescription()));
        product.setPrice(form.getPrice());
        product.setQuantity(form.getQuantity());
        product.setImageUrl(normalizeText(form.getImageUrl()));

        productRepository.update(product);

        productRepository.deleteProductCategories(form.getId());
        productRepository.addProductCategory(form.getId(), form.getCategoryId());
    }

    public void deleteProduct(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Не указан id товара");
        }

        productRepository.deleteById(id);
    }

    private BigDecimal parsePrice(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Цена должна быть числом");
        }
    }

    private void validateProductForm(ProductForm form) {
        if (form.getName() == null || form.getName().isBlank()) {
            throw new IllegalArgumentException("Название товара не может быть пустым");
        }

        if (form.getPrice() == null || form.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Цена товара должна быть положительной");
        }

        if (form.getQuantity() == null || form.getQuantity() < 0) {
            throw new IllegalArgumentException("Количество товара не может быть отрицательным");
        }

        if (form.getCategoryId() == null) {
            throw new IllegalArgumentException("Необходимо выбрать категорию товара");
        }
    }

    private String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}