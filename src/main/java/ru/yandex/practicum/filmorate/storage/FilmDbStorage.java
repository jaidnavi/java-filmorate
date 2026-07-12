package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


import static ru.yandex.practicum.filmorate.model.Film.CINEMA_BIRTHDAY;


@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final FilmMapper FILM_MAPPER = new FilmMapper();

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    @Override
    public Film create(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getMpaId()
        );

        String selectSql = "SELECT film_id FROM films " +
                "WHERE name = ? AND description = ? AND release_date = ? AND duration = ? AND mpa_id = ? " +
                "ORDER BY film_id DESC LIMIT 1";

        Long generatedId = jdbcTemplate.queryForObject(selectSql, Long.class,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getMpaId()
        );

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
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getMpaId(), film.getFilmId());

        if (rowsUpdated == 0) {
            throw new NoDataFoundException("Не найден фильм для обновления с id = " + film.getFilmId());
        }

        return get(film.getFilmId()).orElseThrow(() ->
                new NoDataFoundException("Не найден фильм с id = " + film.getFilmId()));
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT film_id, name, description, release_date, duration, mpa_id " +
                "FROM films " +
                "ORDER BY film_id";

        return jdbcTemplate.query(sql, FILM_MAPPER);
    }

    @Override
    public Optional<Film> get(Long filmId) {
        String sql = "SELECT film_id, name, description, release_date, duration, mpa_id FROM films WHERE film_id = ?";

        List<Film> films = jdbcTemplate.query(sql, FILM_MAPPER, filmId);

        return films.stream().findFirst();
    }

    private static class FilmMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Mpa mpa = new Mpa();
            mpa.setMpaId(rs.getLong("mpa_id"));

            Film film = new Film();
            film.setFilmId(rs.getLong("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(mpa);
            return film;
        }
    }
}
