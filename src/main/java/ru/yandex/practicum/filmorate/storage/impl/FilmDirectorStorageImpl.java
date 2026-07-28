package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.FilmDirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class FilmDirectorStorageImpl implements FilmDirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void replaceByFilmId(Long filmId, Set<Director> directors) {
        if (directors != null) {
            deleteByFilmId(filmId);
            directors.stream()
                    .map(Director::getId)
                    .forEach(id -> addDirector(filmId, id));
        }
    }

    @Override
    public Set<Director> findByFilmId(Long filmId) {
        String sql = """
                SELECT  f.director_id AS director_id,
                        g.name
                FROM film_directors AS f
                JOIN directors AS g ON f.director_id = g.director_id
                WHERE f.film_id = ?
                ORDER BY g.director_id
                """;

        return new LinkedHashSet<>(jdbcTemplate.query(sql, this::mapRowToDirector, filmId));
    }

    @Override
    public void deleteByFilmId(Long filmId) {
        String sql = """
                DELETE FROM film_directors
                WHERE film_id = ?
                """;

        jdbcTemplate.update(
                sql,
                filmId
        );
    }

    @Override
    public void addDirector(Long filmId, Long directorId) {
        String sql = """
                MERGE INTO film_directors (film_id, director_id)
                KEY (film_id, director_id)
                VALUES (?, ?)
                """;

        jdbcTemplate.update(
                sql,
                filmId,
                directorId
        );
    }

    private Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        Director director = new Director();
        director.setId(rs.getLong("director_id"));
        director.setName(rs.getString("name"));
        return director;
    }
}
