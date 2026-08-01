package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;


@Slf4j
@RequiredArgsConstructor
@Component
public class FilmLikeStorageImpl implements FilmLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(Long filmId, Long userId) {
        String sql = """
                MERGE INTO film_likes (film_id, user_id)
                KEY (film_id, user_id)
                VALUES (?, ?)
                """;

        jdbcTemplate.update(
                sql,
                filmId,
                userId
        );
    }

    @Override
    public void delete(Long filmId, Long userId) {
        String sql = """
                DELETE FROM film_likes
                WHERE film_id = ?
                AND user_id = ?
                """;

        int rowsDeleted = jdbcTemplate.update(sql, filmId, userId);

        if (rowsDeleted == 0) {
            throw new NoDataFoundException(
                    String.format("Лайк пользователя с id = %d для фильма с id = %d не найден", userId, filmId)
            );
        }
    }


}
