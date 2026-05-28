package com.example.FloWe.repository;

import com.example.FloWe.model.Order;
import com.example.FloWe.model.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long create(Order order) {
        String sql = """
                INSERT INTO orders
                (user_id, status, total_amount, delivery_address, comment, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getDeliveryAddress(),
                order.getComment(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public void addItem(Long orderId, OrderItem item) {
        String sql = """
                INSERT INTO order_items
                (order_id, product_id, product_name, quantity, unit_price)
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                orderId,
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }

    public List<Order> findAll() {
        String sql = """
                SELECT id, user_id, status, total_amount, delivery_address, comment, created_at, updated_at
                FROM orders
                ORDER BY created_at DESC
                """;

        List<Order> orders = jdbcTemplate.query(sql, this::mapOrder);

        for (Order order : orders) {
            order.setItems(findItemsByOrderId(order.getId()));
        }

        return orders;
    }

    public List<Order> findByUserId(Long userId) {
        String sql = """
                SELECT id, user_id, status, total_amount, delivery_address, comment, created_at, updated_at
                FROM orders
                WHERE user_id = ?
                ORDER BY created_at DESC
                """;

        List<Order> orders = jdbcTemplate.query(sql, this::mapOrder, userId);

        for (Order order : orders) {
            order.setItems(findItemsByOrderId(order.getId()));
        }

        return orders;
    }

    private List<OrderItem> findItemsByOrderId(Long orderId) {
        String sql = """
                SELECT id, order_id, product_id, product_name, quantity, unit_price
                FROM order_items
                WHERE order_id = ?
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, this::mapOrderItem, orderId);
    }

    private Order mapOrder(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();

        order.setId(rs.getLong("id"));
        order.setUserId(rs.getLong("user_id"));
        order.setStatus(rs.getString("status"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setDeliveryAddress(rs.getString("delivery_address"));
        order.setComment(rs.getString("comment"));
        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        order.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);

        return order;
    }

    private OrderItem mapOrderItem(ResultSet rs, int rowNum) throws SQLException {
        return new OrderItem(
                rs.getLong("id"),
                rs.getLong("order_id"),
                rs.getLong("product_id"),
                rs.getString("product_name"),
                rs.getInt("quantity"),
                rs.getBigDecimal("unit_price")
        );
    }
}
