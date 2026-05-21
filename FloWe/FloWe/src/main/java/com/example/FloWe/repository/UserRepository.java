package com.example.FloWe.repository;

import com.example.FloWe.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(User user) {

        String sql = """
                INSERT INTO users
                (first_name, last_name, email, password, role, enabled, balance)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.isEnabled(),
                user.getBalance()
        );
    }

    public boolean existsByEmail(String email) {

        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE email = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);

        return count != null && count > 0;
    }

    public Optional<User> findByEmail(String email) {

        String sql = """
                SELECT id, first_name, last_name, email, password, role, enabled, balance
                FROM users
                WHERE email = ?
                """;

        return jdbcTemplate.query(sql, this::mapRowToUser, email)
                .stream()
                .findFirst();
    }

    public Optional<User> findById(Long id) {

        String sql = """
                SELECT id, first_name, last_name, email, password, role, enabled, balance
                FROM users
                WHERE id = ?
                """;

        return jdbcTemplate.query(sql, this::mapRowToUser, id)
                .stream()
                .findFirst();
    }

    public void enableUser(Long userId) {

        String sql = """
                UPDATE users
                SET enabled = true
                WHERE id = ?
                """;

        jdbcTemplate.update(sql, userId);
    }
    
    public void updatePassword(Long userId, String newPassword) {

        String sql = """
                UPDATE users
                SET password = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(sql, newPassword, userId);
    }
    
    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {

        User user = new User();

        user.setId(rs.getLong("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setEnabled(rs.getBoolean("enabled"));
        user.setBalance(rs.getBigDecimal("balance"));

        return user;
    }
}