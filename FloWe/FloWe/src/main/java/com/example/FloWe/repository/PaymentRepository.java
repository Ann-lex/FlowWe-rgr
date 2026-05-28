package com.example.FloWe.repository;

import com.example.FloWe.model.Payment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class PaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Payment payment) {
        String sql = """
                INSERT INTO payments
                (order_id, user_id, amount, payment_method, status, created_at)
                VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """;

        jdbcTemplate.update(
                sql,
                payment.getOrderId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus()
        );
    }

    public List<Payment> findAll() {
        String sql = """
                SELECT p.id,
                       p.order_id,
                       p.user_id,
                       p.amount,
                       p.payment_method,
                       p.status,
                       p.created_at,
                       u.email AS user_email
                FROM payments p
                JOIN users u ON u.id = p.user_id
                ORDER BY p.created_at DESC
                """;

        return jdbcTemplate.query(sql, this::mapPayment);
    }

    private Payment mapPayment(ResultSet rs, int rowNum) throws SQLException {
        Payment payment = new Payment();

        payment.setId(rs.getLong("id"));
        payment.setOrderId(rs.getLong("order_id"));
        payment.setUserId(rs.getLong("user_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        payment.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);

        payment.setUserEmail(rs.getString("user_email"));

        return payment;
    }
}