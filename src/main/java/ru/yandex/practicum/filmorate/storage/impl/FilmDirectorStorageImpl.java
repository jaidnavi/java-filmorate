package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.FilmDirectorStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class FilmDirectorStorageImpl implements FilmDirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final RowMapper<Director> DIRECTOR_MAPPER = new DataClassRowMapper<>(Director.class);

    @Override
    public void replaceByFilmId(Long filmId, Set<Director> directors) {
        if (directors != null) {
            deleteByFilmId(filmId);

            if (directors.isEmpty()) {
                return;
            }

            String sql = """
                MERGE INTO film_directors (film_id, director_id)
                KEY (film_id, director_id)
                VALUES (?, ?)
                """;

            List<Long> directorIds = directors.stream()
                    .map(Director::getId)
                    .toList();

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, filmId);
                    ps.setLong(2, directorIds.get(i));
                }

                @Override
                public int getBatchSize() {
                    return directorIds.size();
                }
            });
        }
    }

    @Override
    public Set<Director> findByFilmId(Long filmId) {
        String sql = """
                SELECT  f.director_id AS id,
                        g.name
                FROM film_directors AS f
                JOIN directors AS g ON f.director_id = g.director_id
                WHERE f.film_id = ?
                ORDER BY g.director_id
                """;

        List<Director> directors = jdbcTemplate.query(sql, DIRECTOR_MAPPER, filmId);
        return new LinkedHashSet<>(directors);
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

}
