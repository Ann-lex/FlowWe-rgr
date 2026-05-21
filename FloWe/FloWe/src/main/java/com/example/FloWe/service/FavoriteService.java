package com.example.FloWe.service;

import com.example.FloWe.model.FavoriteItem;
import com.example.FloWe.model.User;
import com.example.FloWe.repository.FavoriteRepository;
import com.example.FloWe.repository.ProductRepository;
import com.example.FloWe.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class FavoriteService {

    private static final Set<String> ACCESS_LEVELS = Set.of("PRIVATE", "LINK", "PUBLIC");

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<FavoriteItem> findByUserEmail(String email) {
        User user = getUser(email);
        return favoriteRepository.findByUserId(user.getId());
    }

    public void add(String email, Long productId, String accessLevel) {
        User user = getUser(email);

        productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден"));

        favoriteRepository.add(user.getId(), productId, normalizeAccessLevel(accessLevel));
    }

    public void updateAccessLevel(String email, Long productId, String accessLevel) {
        User user = getUser(email);
        favoriteRepository.updateAccessLevel(user.getId(), productId, normalizeAccessLevel(accessLevel));
    }

    public void delete(String email, Long productId) {
        User user = getUser(email);
        favoriteRepository.delete(user.getId(), productId);
    }

    private String normalizeAccessLevel(String accessLevel) {
        String value = accessLevel == null ? "PRIVATE" : accessLevel.trim().toUpperCase();

        if (!ACCESS_LEVELS.contains(value)) {
            throw new IllegalArgumentException("Некорректный уровень доступа");
        }

        return value;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }
}
