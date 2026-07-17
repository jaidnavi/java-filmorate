package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FilmStorageImpl implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRefStorage genreRefStorage;
    private final FilmLikeStorage filmLikeStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private static final FilmMapper FILM_MAPPER = new FilmMapper();

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmStorageImpl(JdbcTemplate jdbcTemplate, GenreRefStorage genreRefStorage, FilmLikeStorage filmLikeStorage, MpaStorage mpaStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreRefStorage = genreRefStorage;
        this.filmLikeStorage = filmLikeStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Film create(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        if (film.getMpa() != null && film.getMpa().getId() != null) {
            mpaStorage.findById(film.getMpa().getId());
        }

        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            if (film.getMpa() != null) {
                stmt.setLong(5, film.getMpa().getId());
            } else {
                stmt.setNull(5, java.sql.Types.BIGINT);
            }
            return stmt;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        log.info("Фильм успешно создан с id = {}", generatedId);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            log.info("Попытка сохранить жанры для фильма id = {}. Количество: {}", generatedId, film.getGenres().size());

            List<Long> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .distinct()
                    .toList();

            for (Long genreId : genreIds) {
                genreStorage.findById(genreId);
            }

            for (Long genreId : genreIds) {
                genreRefStorage.addGenre(generatedId, genreId);
            }
            log.info("Жанры успешно записаны в таблицу genre_ref");

        } else {
            log.warn("У создаваемого фильма id = {} нет жанров в запросе!", generatedId);
        }

        return get(generatedId).orElseThrow(() ->
                new NoDataFoundException("Ошибка при создании фильма с id = " + generatedId));
    }

    @Override
    public Film update(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        if (rowsUpdated == 0) {
            throw new NoDataFoundException("Не найден фильм для обновления с id = " + film.getId());
        }

        return get(film.getId()).orElseThrow(() ->
                new NoDataFoundException("Не найден фильм с id = " + film.getId()));
    }

    @Override
    public Collection<Film> findAll() {
        String sql = """
                SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                       f.mpa_id, m.name AS mpa_name
                FROM films f
                LEFT JOIN mpa m ON f.mpa_id = m.mpa_id
                """;

        List<Film> films = jdbcTemplate.query(sql, FILM_MAPPER);


        for (Film film : films) {

            film.setGenres(genreRefStorage.findByFilmId(film.getId()));

            film.setLikeUsers(filmLikeStorage.findUserByFilmId(film.getId()));

        }

        return films;
    }

    @Override
    public Optional<Film> get(Long filmId) {
        String sql = """
                SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                       f.mpa_id, m.name AS mpa_name
                FROM films f
                LEFT JOIN mpa m ON f.mpa_id = m.mpa_id
                WHERE f.film_id = ?
                """;

        List<Film> films = jdbcTemplate.query(sql, FILM_MAPPER, filmId);

        if (!films.isEmpty()) {
            Film film = films.get(0);

            film.setGenres(genreRefStorage.findByFilmId(filmId));

            film.setLikeUsers(filmLikeStorage.findUserByFilmId(filmId));

            return Optional.of(film);
        }

        return Optional.empty();
    }

    @Override
    public Collection<Film> findPopular(int count) {

        String sql = """
                SELECT f.film_id
                FROM films f
                LEFT JOIN film_likes fl ON f.film_id = fl.film_id
                GROUP BY f.film_id
                ORDER BY COUNT(fl.user_id) DESC, f.film_id ASC
                LIMIT ?
                """;

        List<Long> popularFilmIds = jdbcTemplate.queryForList(sql, Long.class, count);

        return popularFilmIds.stream()
                .map(filmId -> get(filmId).orElse(null))
                .filter(Objects::nonNull) // Защита от пустых значений
                .collect(Collectors.toList());
    }

    private static class FilmMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));

            long mpaId = rs.getLong("mpa_id");
            if (mpaId != 0) {
                Mpa mpa = new Mpa();
                mpa.setId(mpaId);
                mpa.setName(rs.getString("mpa_name"));
                film.setMpa(mpa);
            }

            return film;
        }
    }
}
