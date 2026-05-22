package com.example.FloWe.service;

import com.example.FloWe.model.Cart;
import com.example.FloWe.model.CartItem;
import com.example.FloWe.model.Order;
import com.example.FloWe.model.OrderItem;
import com.example.FloWe.model.Product;
import com.example.FloWe.model.User;
import com.example.FloWe.repository.CartRepository;
import com.example.FloWe.repository.OrderRepository;
import com.example.FloWe.repository.ProductRepository;
import com.example.FloWe.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void checkout(String email) {
        User user = getUser(email);
        Cart cart = cartRepository.findOrCreateByUserId(user.getId());

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Корзина пуста");
        }

        if (user.getBalance() == null || user.getBalance().compareTo(cart.getTotalPrice()) < 0) {
            throw new IllegalArgumentException("Недостаточно средств на балансе");
        }

        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Товар не найден"));

            if (product.getQuantity() == null || product.getQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("На складе недостаточно товара: " + product.getName());
            }
        }

        LocalDateTime now = LocalDateTime.now();

        Order order = new Order();
        order.setUserId(user.getId());
        order.setStatus("PAID");
        order.setTotalAmount(cart.getTotalPrice());
        order.setDeliveryAddress("Не указан");
        order.setComment(null);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        Long orderId = orderRepository.create(order);

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getPrice());

            orderRepository.addItem(orderId, orderItem);
            productRepository.decreaseQuantity(cartItem.getProductId(), cartItem.getQuantity());
        }

        userRepository.updateBalance(user.getId(), user.getBalance().subtract(cart.getTotalPrice()));
        cartRepository.clear(cart.getId());
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public List<Order> findByUserEmail(String email) {
        User user = getUser(email);
        return orderRepository.findByUserId(user.getId());
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }
}
