package com.example.FloWe.service;

import com.example.FloWe.model.Category;
import com.example.FloWe.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public void addCategory(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название категории не может быть пустым");
        }

        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Такая категория уже существует");
        }

        Category category = new Category();
        category.setName(name.trim());

        categoryRepository.save(category);
    }
}