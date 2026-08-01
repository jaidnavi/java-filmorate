package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.FilmDirectorStorage;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class FilmDirectorStorageImpl implements FilmDirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final RowMapper<Director> DIRECTOR_MAPPER = new DataClassRowMapper<>(Director.class);
    private static final String INSERT = "MERGE INTO film_directors (film_id, director_id) " +
                                                "KEY (film_id, director_id) " +
                                             "VALUES (?, ?)";

    @Override
    public void replaceByFilmId(Long filmId, Set<Director> directors) {
        if (directors != null) {
            deleteByFilmId(filmId);
            if (!directors.isEmpty()) {
                addDirectorsToFilm(directors,filmId);
            }
        }
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
    public void addDirectorsToFilm(Set<Director> directors, Long filmId) {
        List<Object[]> batchArgs = directors.stream()
                .map(Director::getId)
                .map(directorId -> new Object[]{filmId, directorId})
                .toList();
        jdbcTemplate.batchUpdate(INSERT, batchArgs);
    }
}
