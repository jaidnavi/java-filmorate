package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreRefStorage;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class GenreRefStorageImpl implements GenreRefStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT = "MERGE INTO genre_ref (film_id, genre_id) " +
                                                "KEY (film_id, genre_id) " +
                                             "VALUES (?, ?)";

    @Override
    public void replaceByFilmId(Long filmId, Set<Genre> genres) {
        if (genres != null) {
            deleteByFilmId(filmId);
            addGenresToFilm(genres, filmId);
        }
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
    public void addGenresToFilm(Set<Genre> genres, Long filmId) {
        List<Object[]> batchArgs = genres.stream()
                .map(Genre::getId)
                .map(genreId -> new Object[]{filmId, genreId})
                .toList();
        jdbcTemplate.batchUpdate(INSERT, batchArgs);
    }
}
