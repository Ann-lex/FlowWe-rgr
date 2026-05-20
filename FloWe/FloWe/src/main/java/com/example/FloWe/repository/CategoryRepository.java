package com.example.FloWe.repository;

import com.example.FloWe.model.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Category> findAll() {
        String sql = """
                SELECT id, name
                FROM categories
                ORDER BY name
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Category(
                        rs.getLong("id"),
                        rs.getString("name")
                )
        );
    }

    public void save(Category category) {
        String sql = """
                INSERT INTO categories (name)
                VALUES (?)
                """;

        jdbcTemplate.update(sql, category.getName());
    }

    public boolean existsByName(String name) {
        String sql = """
                SELECT COUNT(*)
                FROM categories
                WHERE LOWER(name) = LOWER(?)
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);

        return count != null && count > 0;
    }
}