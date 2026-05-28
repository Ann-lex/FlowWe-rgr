package com.example.FloWe.repository;

import com.example.FloWe.model.StockHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class StockHistoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public StockHistoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(StockHistory history) {
        String sql = """
                INSERT INTO stock_history
                (product_id, product_name, seller_id, user_id, order_id,
                 old_quantity, new_quantity, change_amount, reason, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """;

        jdbcTemplate.update(
                sql,
                history.getProductId(),
                history.getProductName(),
                history.getSellerId(),
                history.getUserId(),
                history.getOrderId(),
                history.getOldQuantity(),
                history.getNewQuantity(),
                history.getChangeAmount(),
                history.getReason()
        );
    }

    public List<StockHistory> findAll() {
        String sql = """
                SELECT sh.id,
                       sh.product_id,
                       sh.product_name,
                       sh.seller_id,
                       sh.user_id,
                       sh.order_id,
                       sh.old_quantity,
                       sh.new_quantity,
                       sh.change_amount,
                       sh.reason,
                       sh.created_at,
                       actor.email AS user_email,
                       seller.email AS seller_email
                FROM stock_history sh
                LEFT JOIN users actor ON actor.id = sh.user_id
                LEFT JOIN users seller ON seller.id = sh.seller_id
                ORDER BY sh.created_at DESC
                """;

        return jdbcTemplate.query(sql, this::mapStockHistory);
    }

    public List<StockHistory> findBySellerId(Long sellerId) {
        String sql = """
                SELECT sh.id,
                       sh.product_id,
                       sh.product_name,
                       sh.seller_id,
                       sh.user_id,
                       sh.order_id,
                       sh.old_quantity,
                       sh.new_quantity,
                       sh.change_amount,
                       sh.reason,
                       sh.created_at,
                       actor.email AS user_email,
                       seller.email AS seller_email
                FROM stock_history sh
                LEFT JOIN users actor ON actor.id = sh.user_id
                LEFT JOIN users seller ON seller.id = sh.seller_id
                WHERE sh.seller_id = ?
                ORDER BY sh.created_at DESC
                """;

        return jdbcTemplate.query(sql, this::mapStockHistory, sellerId);
    }

    private StockHistory mapStockHistory(ResultSet rs, int rowNum) throws SQLException {
        StockHistory history = new StockHistory();

        history.setId(rs.getLong("id"));

        Long productId = rs.getLong("product_id");
        history.setProductId(rs.wasNull() ? null : productId);

        history.setProductName(rs.getString("product_name"));

        Long sellerId = rs.getLong("seller_id");
        history.setSellerId(rs.wasNull() ? null : sellerId);

        Long userId = rs.getLong("user_id");
        history.setUserId(rs.wasNull() ? null : userId);

        Long orderId = rs.getLong("order_id");
        history.setOrderId(rs.wasNull() ? null : orderId);

        history.setOldQuantity((Integer) rs.getObject("old_quantity"));
        history.setNewQuantity((Integer) rs.getObject("new_quantity"));
        history.setChangeAmount(rs.getInt("change_amount"));
        history.setReason(rs.getString("reason"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        history.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);

        history.setUserEmail(rs.getString("user_email"));
        history.setSellerEmail(rs.getString("seller_email"));

        return history;
    }
}