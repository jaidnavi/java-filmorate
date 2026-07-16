package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreRefStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class GenreRefDBStorage implements GenreRefStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void replaceByFilmId(Long filmId, Set<Genre> genres) {
        if (genres != null) {
            deleteByFilmId(filmId);
            genres.stream()
                    .map(Genre::getId)
                    .forEach(id -> addGenre(filmId, id));
        }
    }

    @Override
    public Set<Genre> findByFilmId(Long filmId) {
        String sql = """
                SELECT  f.genre_id AS genre_id,
                        g.genre
                FROM genre_ref AS f
                JOIN genre AS g ON f.genre_id = g.genre_id
                WHERE f.film_id = ?
                ORDER BY g.genre_id
                """;

        return new LinkedHashSet<>(jdbcTemplate.query(sql, this::mapRowToGenre, filmId));
    }

    @Override
    public void deleteByFilmId(Long filmId) {
        String sql = """
                DELETE FROM genre_ref
                WHERE film_id = ?
                """;

        jdbcTemplate.update(
                sql,
                filmId
        );
    }

    @Override
    public void addGenre(Long filmId, Long genreId) {
        String sql = """
                MERGE INTO genre_ref (film_id, genre_id)
                KEY (film_id, genre_id)
                VALUES (?, ?)
                """;

        jdbcTemplate.update(
                sql,
                filmId,
                genreId
        );
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getLong("genre_id"));
        genre.setName(rs.getString("genre"));
        return genre;
    }

}
