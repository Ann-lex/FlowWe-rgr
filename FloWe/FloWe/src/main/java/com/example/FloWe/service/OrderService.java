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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final StockHistoryService stockHistoryService;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository,
                        PaymentService paymentService,
                        StockHistoryService stockHistoryService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
        this.stockHistoryService = stockHistoryService;
    }

    @Transactional
    public void checkout(String email) {
        User user = getUser(email);
        Cart cart = cartRepository.findOrCreateByUserId(user.getId());

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Корзина пуста");
        }

        BigDecimal totalAmount = cart.getTotalPrice();

        if (user.getBalance() == null || user.getBalance().compareTo(totalAmount) < 0) {
            throw new IllegalArgumentException("Недостаточно средств на балансе");
        }

        Map<Long, Product> productsById = new HashMap<>();

        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Товар не найден"));

            if (product.getQuantity() == null || product.getQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("На складе недостаточно товара: " + product.getName());
            }

            productsById.put(product.getId(), product);
        }

        LocalDateTime now = LocalDateTime.now();

        Order order = new Order();
        order.setUserId(user.getId());
        order.setStatus("PAID");
        order.setTotalAmount(totalAmount);
        order.setDeliveryAddress("Не указан");
        order.setComment(null);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        Long orderId = orderRepository.create(order);

        for (CartItem cartItem : cart.getItems()) {
            Product product = productsById.get(cartItem.getProductId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getPrice());

            orderRepository.addItem(orderId, orderItem);

            stockHistoryService.logOrderPurchase(
                    product,
                    user.getId(),
                    orderId,
                    cartItem.getQuantity()
            );

            productRepository.decreaseQuantity(cartItem.getProductId(), cartItem.getQuantity());
        }

        BigDecimal newBalance = user.getBalance().subtract(totalAmount);
        userRepository.updateBalance(user.getId(), newBalance);

        paymentService.createSuccessfulPayment(orderId, user.getId(), totalAmount);

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