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
                SELECT f.id, p.id AS product_id, p.name, p.description, p.price, p.image_url, f.access_level
                FROM favorites f
                JOIN products p ON p.id = f.product_id
                WHERE f.user_id = ?
                ORDER BY f.id DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new FavoriteItem(
                        rs.getLong("id"),
                        rs.getLong("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("price"),
                        rs.getString("image_url"),
                        rs.getString("access_level")
                ), userId);
    }

    public void add(Long userId, Long productId, String accessLevel) {
        String sql = """
                INSERT INTO favorites (user_id, product_id, access_level)
                VALUES (?, ?, ?)
                ON CONFLICT (user_id, product_id)
                DO UPDATE SET access_level = EXCLUDED.access_level
                """;

        jdbcTemplate.update(sql, userId, productId, accessLevel);
    }

    public void updateAccessLevel(Long userId, Long productId, String accessLevel) {
        String sql = """
                UPDATE favorites
                SET access_level = ?
                WHERE user_id = ? AND product_id = ?
                """;

        jdbcTemplate.update(sql, accessLevel, userId, productId);
    }

    public void delete(Long userId, Long productId) {
        String sql = """
                DELETE FROM favorites
                WHERE user_id = ? AND product_id = ?
                """;

        jdbcTemplate.update(sql, userId, productId);
    }
}
