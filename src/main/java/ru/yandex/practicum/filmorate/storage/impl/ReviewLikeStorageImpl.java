package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;

@Component
@RequiredArgsConstructor
public class ReviewLikeStorageImpl implements ReviewLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long reviewId, Long userId) {
        String sql = "MERGE INTO review_likes (review_id, user_id, is_positive) KEY(review_id, user_id) VALUES(?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, true);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        String sql = "MERGE INTO review_likes (review_id, user_id, is_positive) KEY(review_id, user_id) VALUES(?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, false);
    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_positive = true";
        int rowsDeleted = jdbcTemplate.update(sql, reviewId, userId);
        if (rowsDeleted == 0) {
            throw new NoDataFoundException("Лайк к отзыву с id = " + reviewId +
                    " от пользователя с id = " + userId + " не найден.");
        }
    }

    @Override
    public void deleteDislike(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_positive = false";
        int rowsDeleted = jdbcTemplate.update(sql, reviewId, userId);
        if (rowsDeleted == 0) {
            throw new NoDataFoundException("Дизлайк к отзыву с id = " + reviewId +
                    " от пользователя с id = " + userId + " не найден.");
        }
    }

}
