package com.example.FloWe.repository;

import com.example.FloWe.model.PasswordResetToken;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class PasswordResetTokenRepository {

    private final JdbcTemplate jdbcTemplate;

    public PasswordResetTokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(PasswordResetToken passwordResetToken) {
        String sql = """
                INSERT INTO password_reset_tokens (token, user_id, expiry_date)
                VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                passwordResetToken.getToken(),
                passwordResetToken.getUserId(),
                passwordResetToken.getExpiryDate()
        );
    }

    public Optional<PasswordResetToken> findByToken(String token) {
        String sql = """
                SELECT id, token, user_id, expiry_date
                FROM password_reset_tokens
                WHERE token = ?
                """;

        return jdbcTemplate.query(sql, this::mapRowToPasswordResetToken, token)
                .stream()
                .findFirst();
    }

    public void deleteByToken(String token) {
        String sql = """
                DELETE FROM password_reset_tokens
                WHERE token = ?
                """;

        jdbcTemplate.update(sql, token);
    }

    public void deleteByUserId(Long userId) {
        String sql = """
                DELETE FROM password_reset_tokens
                WHERE user_id = ?
                """;

        jdbcTemplate.update(sql, userId);
    }

    private PasswordResetToken mapRowToPasswordResetToken(ResultSet rs, int rowNum) throws SQLException {
        PasswordResetToken passwordResetToken = new PasswordResetToken();

        passwordResetToken.setId(rs.getLong("id"));
        passwordResetToken.setToken(rs.getString("token"));
        passwordResetToken.setUserId(rs.getLong("user_id"));
        passwordResetToken.setExpiryDate(rs.getTimestamp("expiry_date").toLocalDateTime());

        return passwordResetToken;
    }
}