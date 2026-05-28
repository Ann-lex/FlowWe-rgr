package com.example.FloWe.repository;

import com.example.FloWe.model.VerificationToken;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class VerificationTokenRepository {

    private final JdbcTemplate jdbcTemplate;

    public VerificationTokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(VerificationToken verificationToken) {
        String sql = """
                INSERT INTO verification_tokens (token, user_id, expiry_date)
                VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                verificationToken.getToken(),
                verificationToken.getUserId(),
                verificationToken.getExpiryDate()
        );
    }

    public Optional<VerificationToken> findByToken(String token) {
        String sql = """
                SELECT id, token, user_id, expiry_date
                FROM verification_tokens
                WHERE token = ?
                """;

        return jdbcTemplate.query(sql, this::mapRowToVerificationToken, token)
                .stream()
                .findFirst();
    }

    public void deleteByToken(String token) {
        String sql = """
                DELETE FROM verification_tokens
                WHERE token = ?
                """;

        jdbcTemplate.update(sql, token);
    }

    public void deleteByUserId(Long userId) {
        String sql = """
            DELETE FROM verification_tokens
            WHERE user_id = ?
            """;

        jdbcTemplate.update(sql, userId);
    }

    private VerificationToken mapRowToVerificationToken(ResultSet rs, int rowNum) throws SQLException {
        VerificationToken verificationToken = new VerificationToken();

        verificationToken.setId(rs.getLong("id"));
        verificationToken.setToken(rs.getString("token"));
        verificationToken.setUserId(rs.getLong("user_id"));
        verificationToken.setExpiryDate(rs.getTimestamp("expiry_date").toLocalDateTime());

        return verificationToken;
    }
}