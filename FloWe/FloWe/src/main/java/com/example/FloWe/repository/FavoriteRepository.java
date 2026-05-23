package com.example.FloWe.repository;

import com.example.FloWe.model.FavoriteItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FavoriteRepository {

    private final JdbcTemplate jdbcTemplate;

    public FavoriteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<FavoriteItem> findByUserId(Long userId) {

        String sql = """
                SELECT f.id,
                       p.id AS product_id,
                       p.name,
                       p.description,
                       p.price,
                       p.image_url
                FROM favorites f
                JOIN products p ON p.id = f.product_id
                WHERE f.user_id = ?
                ORDER BY f.id DESC
                """;

        return jdbcTemplate.query(
                sql,
                new Object[]{userId},
                (rs, rowNum) -> {

                    FavoriteItem item = new FavoriteItem();

                    item.setId(rs.getLong("id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setName(rs.getString("name"));
                    item.setDescription(rs.getString("description"));
                    item.setPrice(rs.getBigDecimal("price"));
                    item.setImageUrl(rs.getString("image_url"));

                    return item;
                }
        );
    }

    public void add(Long userId, Long productId) {

        String sql = """
                INSERT INTO favorites(user_id, product_id)
                VALUES (?, ?)
                ON CONFLICT DO NOTHING
                """;

        jdbcTemplate.update(sql, userId, productId);
    }

    public void remove(Long userId, Long productId) {

        String sql = """
                DELETE FROM favorites
                WHERE user_id = ?
                AND product_id = ?
                """;

        jdbcTemplate.update(sql, userId, productId);
    }

    public boolean exists(Long userId, Long productId) {

        String sql = """
                SELECT COUNT(*)
                FROM favorites
                WHERE user_id = ?
                AND product_id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                userId,
                productId
        );

        return count != null && count > 0;
    }
}