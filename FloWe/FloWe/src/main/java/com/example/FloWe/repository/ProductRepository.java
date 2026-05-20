package com.example.FloWe.repository;

import com.example.FloWe.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Product> findAll() {
        String sql = """
                SELECT id, name, description, price, quantity, image_url, seller_id
                FROM products
                ORDER BY id DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Product(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("price"),
                        rs.getInt("quantity"),
                        rs.getString("image_url"),
                        rs.getObject("seller_id", Long.class)
                )
        );
    }

    public Optional<Product> findById(Long id) {
        String sql = """
                SELECT id, name, description, price, quantity, image_url, seller_id
                FROM products
                WHERE id = ?
                """;

        List<Product> products = jdbcTemplate.query(sql, (rs, rowNum) ->
                        new Product(
                                rs.getLong("id"),
                                rs.getString("name"),
                                rs.getString("description"),
                                rs.getBigDecimal("price"),
                                rs.getInt("quantity"),
                                rs.getString("image_url"),
                                rs.getObject("seller_id", Long.class)
                        ),
                id
        );

        return products.stream().findFirst();
    }

    public List<Product> search(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        StringBuilder sql = new StringBuilder("""
                SELECT DISTINCT p.id, p.name, p.description, p.price, p.quantity, p.image_url, p.seller_id
                FROM products p
                """);

        List<Object> params = new ArrayList<>();

        if (categoryId != null) {
            sql.append("""
                    JOIN product_categories pc ON p.id = pc.product_id
                    """);
        }

        sql.append(" WHERE 1 = 1 ");

        if (keyword != null && !keyword.isBlank()) {
            sql.append("""
                    AND (
                        LOWER(p.name) LIKE LOWER(?)
                        OR LOWER(p.description) LIKE LOWER(?)
                    )
                    """);

            String searchValue = "%" + keyword.trim() + "%";
            params.add(searchValue);
            params.add(searchValue);
        }

        if (categoryId != null) {
            sql.append(" AND pc.category_id = ? ");
            params.add(categoryId);
        }

        if (minPrice != null) {
            sql.append(" AND p.price >= ? ");
            params.add(minPrice);
        }

        if (maxPrice != null) {
            sql.append(" AND p.price <= ? ");
            params.add(maxPrice);
        }

        sql.append(" ORDER BY p.id DESC ");

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) ->
                        new Product(
                                rs.getLong("id"),
                                rs.getString("name"),
                                rs.getString("description"),
                                rs.getBigDecimal("price"),
                                rs.getInt("quantity"),
                                rs.getString("image_url"),
                                rs.getObject("seller_id", Long.class)
                        ),
                params.toArray()
        );
    }
}