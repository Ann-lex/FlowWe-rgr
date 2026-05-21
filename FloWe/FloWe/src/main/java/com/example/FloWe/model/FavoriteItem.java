package com.example.FloWe.model;

import java.math.BigDecimal;

public class FavoriteItem {

    private Long id;
    private Long productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private String accessLevel;

    public FavoriteItem() {
    }

    public FavoriteItem(Long id, Long productId, String productName, String description,
                        BigDecimal price, String imageUrl, String accessLevel) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.accessLevel = accessLevel;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAccessLevel() {
        return accessLevel;
    }
}
