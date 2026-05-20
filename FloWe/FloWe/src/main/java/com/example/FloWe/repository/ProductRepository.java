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

    public List<Product> findAllBySellerId(Long sellerId) {
        String sql = """
                SELECT id, name, description, price, quantity, image_url, seller_id
                FROM products
                WHERE seller_id = ?
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
                        ),
                sellerId
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

    public Optional<Product> findByIdAndSellerId(Long id, Long sellerId) {
        String sql = """
                SELECT id, name, description, price, quantity, image_url, seller_id
                FROM products
                WHERE id = ?
                  AND seller_id = ?
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
                id,
                sellerId
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
            sql.append(" JOIN product_categories pc ON p.id = pc.product_id ");
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

    public Long save(Product product) {
        String sql = """
                INSERT INTO products (name, description, price, quantity, image_url, seller_id)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getImageUrl(),
                product.getSellerId()
        );
    }

    public int updateBySellerId(Product product, Long sellerId) {
        String sql = """
                UPDATE products
                SET name = ?,
                    description = ?,
                    price = ?,
                    quantity = ?,
                    image_url = ?
                WHERE id = ?
                  AND seller_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getImageUrl(),
                product.getId(),
                sellerId
        );
    }

    public int deleteByIdAndSellerId(Long id, Long sellerId) {
        String sql = """
                DELETE FROM products
                WHERE id = ?
                  AND seller_id = ?
                """;

        return jdbcTemplate.update(sql, id, sellerId);
    }

    public void addProductCategory(Long productId, Long categoryId) {
        String sql = """
                INSERT INTO product_categories (product_id, category_id)
                VALUES (?, ?)
                ON CONFLICT DO NOTHING
                """;

        jdbcTemplate.update(sql, productId, categoryId);
    }

    public void deleteProductCategories(Long productId) {
        String sql = """
                DELETE FROM product_categories
                WHERE product_id = ?
                """;

        jdbcTemplate.update(sql, productId);
    }

    public Optional<Long> findCategoryIdByProductIdAndSellerId(Long productId, Long sellerId) {
        String sql = """
                SELECT pc.category_id
                FROM product_categories pc
                JOIN products p ON p.id = pc.product_id
                WHERE pc.product_id = ?
                  AND p.seller_id = ?
                LIMIT 1
                """;

        List<Long> categoryIds = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getLong("category_id"),
                productId,
                sellerId
        );

        return categoryIds.stream().findFirst();
    }
}