package com.example.FloWe.service;

import com.example.FloWe.model.Cart;
import com.example.FloWe.model.CartItem;
import com.example.FloWe.model.Product;
import com.example.FloWe.model.User;
import com.example.FloWe.repository.CartRepository;
import com.example.FloWe.repository.ProductRepository;
import com.example.FloWe.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Cart getCart(String email) {
        User user = getUser(email);
        return cartRepository.findOrCreateByUserId(user.getId());
    }

    public void addItem(String email, Long productId, Integer quantity) {
        int amount = quantity == null ? 1 : quantity;

        if (amount <= 0) {
            throw new IllegalArgumentException("Количество должно быть больше нуля");
        }

        User user = getUser(email);
        Cart cart = cartRepository.findOrCreateByUserId(user.getId());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден"));

        if (product.getQuantity() == null || product.getQuantity() <= 0) {
            throw new IllegalArgumentException("Товара нет в наличии");
        }

        CartItem existingItem = cartRepository.findItem(cart.getId(), productId).orElse(null);

        if (existingItem == null) {
            cartRepository.addItem(cart.getId(), productId, product.getName(), product.getPrice(), amount);
        } else {
            cartRepository.updateItemQuantity(
                    existingItem.getId(),
                    cart.getId(),
                    existingItem.getQuantity() + amount
            );
        }
    }

    public void updateQuantity(String email, Long itemId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Количество должно быть больше нуля");
        }

        Cart cart = getCart(email);
        cartRepository.updateItemQuantity(itemId, cart.getId(), quantity);
    }

    public void deleteItem(String email, Long itemId) {
        Cart cart = getCart(email);
        cartRepository.deleteItem(itemId, cart.getId());
    }

    public void clear(String email) {
        Cart cart = getCart(email);
        cartRepository.clear(cart.getId());
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }
}