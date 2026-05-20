package com.example.FloWe.service;

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
}