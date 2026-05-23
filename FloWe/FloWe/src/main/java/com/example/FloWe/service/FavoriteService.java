package com.example.FloWe.service;

import com.example.FloWe.model.FavoriteItem;
import com.example.FloWe.model.User;
import com.example.FloWe.repository.FavoriteRepository;
import com.example.FloWe.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
    }

    public List<FavoriteItem> findByUserEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return favoriteRepository.findByUserId(user.getId());
    }

    public void add(String email, Long productId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        favoriteRepository.add(user.getId(), productId);
    }

    public void remove(String email, Long productId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        favoriteRepository.remove(user.getId(), productId);
    }
}