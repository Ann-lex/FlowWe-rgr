package com.example.FloWe.repository;

import com.example.FloWe.model.Review;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class ReviewRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReviewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Review> findByProductId(Long productId) {
        String sql = """
                SELECT r.id,
                       r.user_id,
                       r.product_id,
                       r.rating,
                       r.text,
                       r.created_at,
                       CONCAT(u.first_name, ' ', u.last_name) AS user_name
                FROM reviews r
                JOIN users u ON u.id = r.user_id
                WHERE r.product_id = ?
                ORDER BY r.created_at DESC
                """;

        return jdbcTemplate.query(sql, this::mapReview, productId);
    }

    public void save(Review review) {
        String sql = """
                INSERT INTO reviews (user_id, product_id, rating, text, created_at)
                VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
                """;

        jdbcTemplate.update(
                sql,
                review.getUserId(),
                review.getProductId(),
                review.getRating(),
                review.getText()
        );
    }

    public boolean existsByProductIdAndUserId(Long productId, Long userId) {
        String sql = """
                SELECT COUNT(*)
                FROM reviews
                WHERE product_id = ?
                  AND user_id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, productId, userId);

        return count != null && count > 0;
    }

    public long countByProductId(Long productId) {
        String sql = """
                SELECT COUNT(*)
                FROM reviews
                WHERE product_id = ?
                """;

        Long count = jdbcTemplate.queryForObject(sql, Long.class, productId);

        return count != null ? count : 0;
    }

    public BigDecimal getAverageRating(Long productId) {
        String sql = """
                SELECT COALESCE(AVG(rating), 0)
                FROM reviews
                WHERE product_id = ?
                """;

        BigDecimal average = jdbcTemplate.queryForObject(sql, BigDecimal.class, productId);

        if (average == null) {
            return BigDecimal.ZERO;
        }

        return average.setScale(1, RoundingMode.HALF_UP);
    }

    private Review mapReview(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();

        review.setId(rs.getLong("id"));
        review.setUserId(rs.getLong("user_id"));
        review.setProductId(rs.getLong("product_id"));
        review.setRating(rs.getInt("rating"));
        review.setText(rs.getString("text"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        review.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);

        review.setUserName(rs.getString("user_name"));

        return review;
    }
}