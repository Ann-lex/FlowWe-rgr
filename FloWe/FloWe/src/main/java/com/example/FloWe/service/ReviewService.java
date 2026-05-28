package com.example.FloWe.service;

import com.example.FloWe.model.Review;
import com.example.FloWe.model.User;
import com.example.FloWe.repository.ProductRepository;
import com.example.FloWe.repository.ReviewRepository;
import com.example.FloWe.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         UserRepository userRepository,
                         ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public List<Review> findByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    public long countReviews(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    public BigDecimal getAverageRating(Long productId) {
        return reviewRepository.getAverageRating(productId);
    }

    public void addReview(Long productId, String userEmail, Integer rating, String text) {
        if (productId == null) {
            throw new IllegalArgumentException("Товар не найден");
        }

        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (!"BUYER".equals(user.getRole())) {
            throw new IllegalArgumentException("Отзывы могут оставлять только покупатели");
        }

        productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден"));

        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Оценка должна быть от 1 до 5");
        }

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Текст отзыва не может быть пустым");
        }

        String normalizedText = text.trim();

        if (normalizedText.length() > 1000) {
            throw new IllegalArgumentException("Отзыв не должен быть длиннее 1000 символов");
        }

        if (reviewRepository.existsByProductIdAndUserId(productId, user.getId())) {
            throw new IllegalArgumentException("Вы уже оставили отзыв на этот товар");
        }

        Review review = new Review();
        review.setProductId(productId);
        review.setUserId(user.getId());
        review.setRating(rating);
        review.setText(normalizedText);

        reviewRepository.save(review);
    }
}