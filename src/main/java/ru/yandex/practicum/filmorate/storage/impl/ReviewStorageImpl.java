package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
@Slf4j
public class ReviewStorageImpl implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review create(Review review) {
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            return stmt;
        }, keyHolder);

        Number key = keyHolder.getKey();

        if (key == null) {
            log.warn("Ошибка в работе с БД");
            throw new InternalServerException("Не удалось сохранить данные");
        }
        Long generatedId = keyHolder.getKey().longValue();
        review.setReviewId(generatedId);

        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ?  WHERE review_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql,
                review.getContent(), review.getIsPositive(), review.getReviewId());

        if (rowsUpdated == 0) {
            log.warn("Ошибка в работе с БД, не удалось обновить отзыв");
            throw new InternalServerException("Не удалось обновить отзыв с id = " + review.getReviewId());
        }

        return get(review.getReviewId()).orElseThrow(() ->
                new InternalServerException("Ошибка в работе с БД, не удалось получить обновленный отзыв"));
    }

    @Override
    public Collection<Review> findAll(int count) {
        String sql = "SELECT r.*, " +
                "  COALESCE(SUM(CASE WHEN rl.is_positive = TRUE THEN 1 " +
                "                    WHEN rl.is_positive = FALSE THEN -1 END), 0) AS useful " +
                "FROM reviews r " +
                "LEFT JOIN review_likes rl ON r.review_id = rl.review_id " +
                "GROUP BY r.review_id " +
                "ORDER BY useful DESC, r.review_id DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Review review = mapRowToReview(rs, rowNum);

            review.setUseful(rs.getInt("useful"));

            return review;
        }, count);
    }

    @Override
    public Collection<Review> findReviewsByFilmId(Long filmId, int count) {
        String sql = "SELECT r.*, " +
                "  COALESCE(SUM(CASE WHEN rl.is_positive = TRUE THEN 1 " +
                "                    WHEN rl.is_positive = FALSE THEN -1 END), 0) AS useful " +
                "FROM reviews r " +
                "LEFT JOIN review_likes rl ON r.review_id = rl.review_id " +
                "WHERE r.film_id = ? " +
                "GROUP BY r.review_id " +
                "ORDER BY useful DESC, r.review_id DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Review review = mapRowToReview(rs, rowNum);

            review.setUseful(rs.getInt("useful"));

            return review;
        }, filmId, count);
    }

    @Override
    public Optional<Review> get(Long reviewId) {
        String sql = "SELECT r.*, " +
                "  COALESCE(SUM(CASE WHEN rl.is_positive = TRUE THEN 1 " +
                "                    WHEN rl.is_positive = FALSE THEN -1 END), 0) AS useful " +
                "FROM reviews r " +
                "LEFT JOIN review_likes rl ON r.review_id = rl.review_id " +
                "WHERE r.review_id = ? " +
                "GROUP BY r.review_id";

        try {
            Review review = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Review r = mapRowToReview(rs, rowNum);
                r.setUseful(rs.getInt("useful"));
                return r;
            }, reviewId);

            return Optional.of(review);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteReviewById(Long reviewId) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        int rowsDeleted = jdbcTemplate.update(sql, reviewId);
        if (rowsDeleted == 0) {
            throw new NoDataFoundException("Не удалось удалить данные. Не найден отзыв с идентификатором " + reviewId);
        }
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getLong("review_id"));
        review.setContent(rs.getString("content"));
        review.setIsPositive(rs.getBoolean("is_positive"));
        review.setUserId(rs.getLong("user_id"));
        review.setFilmId(rs.getLong("film_id"));

        return review;
    }

}
