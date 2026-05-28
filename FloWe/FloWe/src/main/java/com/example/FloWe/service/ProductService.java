package com.example.FloWe.service;

import com.example.FloWe.dto.ProductForm;
import com.example.FloWe.model.Product;
import com.example.FloWe.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StockHistoryService stockHistoryService;

    public ProductService(ProductRepository productRepository,
                          StockHistoryService stockHistoryService) {
        this.productRepository = productRepository;
        this.stockHistoryService = stockHistoryService;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findBySellerId(Long sellerId) {
        validateSellerId(sellerId);

        return productRepository.findAllBySellerId(sellerId);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> search(String keyword, Long categoryId, String minPrice, String maxPrice) {
        BigDecimal min = parsePrice(minPrice);
        BigDecimal max = parsePrice(maxPrice);

        return productRepository.search(keyword, categoryId, min, max);
    }

    @Transactional
    public void createProduct(ProductForm form, Long sellerId) {
        validateSellerId(sellerId);
        validateProductForm(form);

        Product product = new Product();
        product.setName(form.getName().trim());
        product.setDescription(normalizeText(form.getDescription()));
        product.setPrice(form.getPrice());
        product.setQuantity(form.getQuantity());
        product.setImageUrl(saveImageFile(form.getImageFile()));
        product.setSellerId(sellerId);

        Long productId = productRepository.save(product);
        product.setId(productId);

        productRepository.addProductCategory(productId, form.getCategoryId());

        stockHistoryService.logProductCreated(product, sellerId);
    }

    @Transactional
    public void updateProduct(ProductForm form, Long sellerId) {
        validateSellerId(sellerId);

        if (form.getId() == null) {
            throw new IllegalArgumentException("Не указан id товара");
        }

        validateProductForm(form);

        Product oldProduct = productRepository.findByIdAndSellerId(form.getId(), sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден или не принадлежит текущему продавцу"));

        Product product = new Product();
        product.setId(form.getId());
        product.setName(form.getName().trim());
        product.setDescription(normalizeText(form.getDescription()));
        product.setPrice(form.getPrice());
        product.setQuantity(form.getQuantity());
        product.setSellerId(sellerId);

        String newImageUrl = saveImageFile(form.getImageFile());

        if (newImageUrl == null) {
            product.setImageUrl(normalizeText(form.getImageUrl()));
        } else {
            product.setImageUrl(newImageUrl);
        }

        int updatedRows = productRepository.updateBySellerId(product, sellerId);

        if (updatedRows == 0) {
            throw new IllegalArgumentException("Товар не найден или не принадлежит текущему продавцу");
        }

        productRepository.deleteProductCategories(form.getId());
        productRepository.addProductCategory(form.getId(), form.getCategoryId());

        stockHistoryService.logSellerUpdate(oldProduct, product, sellerId);
    }

    @Transactional
    public void deleteProduct(Long id, Long sellerId) {
        validateSellerId(sellerId);

        if (id == null) {
            throw new IllegalArgumentException("Не указан id товара");
        }

        Product product = productRepository.findByIdAndSellerId(id, sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден или не принадлежит текущему продавцу"));

        stockHistoryService.logProductDeleted(product, sellerId);

        int deletedRows = productRepository.deleteByIdAndSellerId(id, sellerId);

        if (deletedRows == 0) {
            throw new IllegalArgumentException("Товар не найден или не принадлежит текущему продавцу");
        }
    }

    public ProductForm getProductFormForEdit(Long id, Long sellerId) {
        validateSellerId(sellerId);

        Product product = productRepository.findByIdAndSellerId(id, sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден или не принадлежит текущему продавцу"));

        ProductForm form = new ProductForm();
        form.setId(product.getId());
        form.setName(product.getName());
        form.setDescription(product.getDescription());
        form.setPrice(product.getPrice());
        form.setQuantity(product.getQuantity());
        form.setImageUrl(product.getImageUrl());

        productRepository.findCategoryIdByProductIdAndSellerId(id, sellerId)
                .ifPresent(form::setCategoryId);

        return form;
    }

    @Transactional
    public void deleteProductByAdmin(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Не указан id товара");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден"));

        stockHistoryService.logProductDeletedByAdmin(product);

        int deletedRows = productRepository.deleteById(id);

        if (deletedRows == 0) {
            throw new IllegalArgumentException("Товар не найден");
        }
    }

    private String saveImageFile(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        String contentType = imageFile.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Можно загружать только изображения");
        }

        try {
            String originalFilename = imageFile.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String fileName = UUID.randomUUID() + extension;

            Path uploadPath = Paths.get("uploads", "products").toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(fileName);
            imageFile.transferTo(filePath.toFile());

            return "/uploads/products/" + fileName;
        } catch (IOException e) {
            throw new IllegalArgumentException("Не удалось загрузить изображение");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }

        int dotIndex = filename.lastIndexOf(".");

        if (dotIndex == -1) {
            return "";
        }

        return filename.substring(dotIndex);
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

        if (form.getPrice() == null || form.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Цена товара должна быть больше нуля");
        }

        if (form.getQuantity() == null || form.getQuantity() < 0) {
            throw new IllegalArgumentException("Количество товара не может быть отрицательным");
        }

        if (form.getCategoryId() == null) {
            throw new IllegalArgumentException("Необходимо выбрать категорию товара");
        }
    }

    private void validateSellerId(Long sellerId) {
        if (sellerId == null) {
            throw new IllegalArgumentException("Продавец не найден");
        }
    }

    private String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}