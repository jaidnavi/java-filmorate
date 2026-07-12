package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.storage.dao.FilmLikeDao;

import static java.lang.String.format;

public class FilmLikeDaoImpl implements FilmLikeDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmLikeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        try {
            jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
        } catch (
                EmptyResultDataAccessException e) {
            throw new NoDataFoundException("Неверный идентификатор пользователя или фильма");
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        try {
            int i = jdbcTemplate.queryForObject(format(
                    "SELECT 1 FROM film_likes WHERE film_id=%d AND user_id=%d", filmId, userId), int.class);
        } catch (
                EmptyResultDataAccessException e) {
            throw new NoDataFoundException("Пользователь с id = " + userId + " не лайкал фильм c id = " + filmId);
        }
        jdbcTemplate.update("DELETE FROM film_likes WHERE film_id=? AND user_id=?", filmId, userId);
    }

}
