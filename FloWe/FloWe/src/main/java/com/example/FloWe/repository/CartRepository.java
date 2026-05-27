package com.example.FloWe.repository;

import com.example.FloWe.model.Cart;
import com.example.FloWe.model.CartItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CartRepository {

    private final JdbcTemplate jdbcTemplate;

    public CartRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Cart findOrCreateByUserId(Long userId) {
        Optional<Long> cartId = findCartIdByUserId(userId);
        Long id = cartId.orElseGet(() -> createCart(userId));

        return new Cart(id, userId, findItemsByCartId(id));
    }

    public Optional<CartItem> findItem(Long cartId, Long productId) {
        String sql = """
                SELECT id, cart_id, product_id, product_name, price, quantity
                FROM cart_items
                WHERE cart_id = ? AND product_id = ?
                """;

        return jdbcTemplate.query(sql, this::mapItem, cartId, productId)
                .stream()
                .findFirst();
    }

    public void addItem(Long cartId, Long productId, String productName, java.math.BigDecimal price, int quantity) {
        String sql = """
                INSERT INTO cart_items (cart_id, product_id, product_name, price, quantity)
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql, cartId, productId, productName, price, quantity);
    }

    public void updateItemQuantity(Long itemId, Long cartId, int quantity) {
        String sql = """
                UPDATE cart_items
                SET quantity = ?
                WHERE id = ? AND cart_id = ?
                """;

        jdbcTemplate.update(sql, quantity, itemId, cartId);
    }

    public void deleteItem(Long itemId, Long cartId) {
        String sql = """
                DELETE FROM cart_items
                WHERE id = ? AND cart_id = ?
                """;

        jdbcTemplate.update(sql, itemId, cartId);
    }

    public void clear(Long cartId) {
        jdbcTemplate.update("DELETE FROM cart_items WHERE cart_id = ?", cartId);
    }

    private Optional<Long> findCartIdByUserId(Long userId) {
        String sql = "SELECT id FROM carts WHERE user_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("id"), userId)
                .stream()
                .findFirst();
    }

    private Long createCart(Long userId) {
        String sql = """
                INSERT INTO carts (user_id)
                VALUES (?)
                RETURNING id
                """;

        return jdbcTemplate.queryForObject(sql, Long.class, userId);
    }

    private List<CartItem> findItemsByCartId(Long cartId) {

        String sql = """
                SELECT id, cart_id, product_id, product_name, price, quantity
                FROM cart_items
                WHERE cart_id = ?
                ORDER BY id DESC
                """;

        return jdbcTemplate.query(
                sql,
                new Object[]{cartId},
                (rs, rowNum) -> {
                    CartItem item = new CartItem();

                    item.setId(rs.getLong("id"));
                    item.setCartId(rs.getLong("cart_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setPrice(rs.getBigDecimal("price"));
                    item.setQuantity(rs.getInt("quantity"));

                    return item;
                }
        );
    }
    
    private CartItem mapItem(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new CartItem(
                rs.getLong("id"),
                rs.getLong("cart_id"),
                rs.getLong("product_id"),
                rs.getString("product_name"),
                rs.getBigDecimal("price"),
                rs.getInt("quantity")
        );
    }
}
