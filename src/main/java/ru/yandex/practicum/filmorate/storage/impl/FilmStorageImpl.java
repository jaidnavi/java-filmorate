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
import ru.yandex.practicum.filmorate.model.*;
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
    private final DirectorStorage directorStorage;
    private final FilmDirectorStorage filmDirectorStorage;
    private static final FilmMapper FILM_MAPPER = new FilmMapper();

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmStorageImpl(JdbcTemplate jdbcTemplate, GenreRefStorage genreRefStorage, FilmLikeStorage filmLikeStorage, MpaStorage mpaStorage, GenreStorage genreStorage, DirectorStorage directorStorage, FilmDirectorStorage filmDirectorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreRefStorage = genreRefStorage;
        this.filmLikeStorage = filmLikeStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage;
        this.filmDirectorStorage = filmDirectorStorage;
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


        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            log.info("Попытка сохранить режисеров для фильма id = {}. Количество: {}", generatedId, film.getDirectors().size());

            List<Long> directorIds = film.getDirectors().stream()
                    .map(Director::getId)
                    .distinct()
                    .toList();

            for (Long directorId : directorIds) {
                directorStorage.get(directorId);
            }

            for (Long directorId : directorIds) {
                filmDirectorStorage.addDirector(generatedId, directorId);
            }
            log.info("Режисеры успешно записаны в таблицу film_directors");

        } else {
            log.warn("У создаваемого фильма id = {} нет режиссеров в запросе!", generatedId);
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

        genreRefStorage.replaceByFilmId(film.getId(), film.getGenres());
        filmDirectorStorage.replaceByFilmId(film.getId(), film.getDirectors());

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

            film.setDirectors(filmDirectorStorage.findByFilmId(film.getId()));

        }

        return films;
    }

    @Override
    public Collection<Film> getByDirector(Long directorId, String sortBy) {

        String sqlOrder = "";
        if (SortType.YEAR.getDescription().equalsIgnoreCase(sortBy)) {
            sqlOrder = """
                    ORDER BY f.release_date ASC
                    """;
        } else if (SortType.LIKES.getDescription().equalsIgnoreCase(sortBy)) {
            sqlOrder = """
                    ORDER BY like_count DESC
                    """;
        }

        String sql = """
                SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                       f.mpa_id, m.name AS mpa_name,
                       COUNT(fl.user_id) AS like_count
                FROM films f
                INNER JOIN  film_directors d ON d.film_id = f.film_id
                LEFT JOIN mpa m ON f.mpa_id = m.mpa_id
                LEFT JOIN film_likes fl ON f.film_id = fl.film_id
                WHERE d.director_id = ?
                GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name
                """ + sqlOrder;

        List<Film> films = jdbcTemplate.query(sql, FILM_MAPPER, directorId);


        for (Film film : films) {

            film.setGenres(genreRefStorage.findByFilmId(film.getId()));

            film.setLikeUsers(filmLikeStorage.findUserByFilmId(film.getId()));

            film.setDirectors(filmDirectorStorage.findByFilmId(film.getId()));

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

            film.setDirectors(filmDirectorStorage.findByFilmId(filmId));

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

    public Collection<Film> findRecommendations(Long userId) {
        String sql = """
                WITH ranked_users AS (
                SELECT
                    fl2.user_id,
                    DENSE_RANK() OVER (ORDER BY COUNT(*) DESC) as rnk
                FROM film_likes fl1
                JOIN FILM_LIKES fl2 ON fl1.film_id = fl2.film_id
                WHERE fl1.user_id = ?
                  AND fl2.user_id != ?
                GROUP BY fl2.user_id
                ),
                top_similar_users AS (
                    SELECT user_id
                    FROM ranked_users
                    WHERE rnk = 1
                )
                SELECT DISTINCT fl3.film_id
                FROM film_likes fl3
                WHERE fl3.user_id IN (SELECT user_id FROM top_similar_users)
                  AND NOT EXISTS (
                      SELECT 1
                      FROM film_likes fl4
                      WHERE fl4.user_id = ?
                        AND fl4.film_id = fl3.film_id
                  )
                """;

        List<Long> recommendationsFilmIds = jdbcTemplate.queryForList(sql, Long.class, userId, userId, userId);

        return recommendationsFilmIds.stream()
                .map(filmId -> get(filmId).orElse(null))
                .filter(Objects::nonNull) // Защита от пустых значений
                .collect(Collectors.toList());

    }

    @Override
    public Collection<Film> search(String query, List<String> by) {
        String lowerQuery = query.toLowerCase();

        Set<SearchByType> searchTypes = by.stream()
                .map(String::toUpperCase)
                .map(SearchByType::valueOf)
                .collect(Collectors.toSet());

        String sqlSelectMain = """
                SELECT f.film_id
                FROM films f
                """;

        String sqlSelectAdd = "";
        String sqlWhere;
        Object[] params;

        if (searchTypes.contains(SearchByType.DIRECTOR) && searchTypes.contains(SearchByType.TITLE)) {
            sqlSelectAdd = """
                    LEFT JOIN film_directors fd ON fd.film_id = f.film_id
                    LEFT JOIN directors d ON d.director_id = fd.director_id
                    """;
            sqlWhere = " WHERE (LOWER(d.name) LIKE ? OR LOWER(f.name) LIKE ?) ";
            params = new Object[]{"%" + lowerQuery + "%", "%" + lowerQuery + "%"};
        } else if (searchTypes.contains(SearchByType.DIRECTOR)) {
            sqlSelectAdd = """
                    LEFT JOIN film_directors fd ON fd.film_id = f.film_id
                    LEFT JOIN directors d ON d.director_id = fd.director_id
                    """;
            sqlWhere = " WHERE LOWER(d.name) LIKE ? ";
            params = new Object[]{"%" + lowerQuery + "%"};
        } else if (searchTypes.contains(SearchByType.TITLE)) {
            sqlWhere = " WHERE LOWER(f.name) LIKE ? ";
            params = new Object[]{"%" + lowerQuery + "%"};
        } else {
            throw new ValidationException("Поиск по полю " + by + " не реализован");
        }

        String sqlOrder = """
                ORDER BY f.film_id DESC
                """;

        String sql = sqlSelectMain + sqlSelectAdd + sqlWhere + sqlOrder;
        List<Long> searchFilmIds = jdbcTemplate.queryForList(sql, Long.class, params);

        return searchFilmIds.stream()
                .map(filmId -> get(filmId).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long filmId) {
        String sql = """
                DELETE FROM films
                WHERE film_id = ?
                """;

        jdbcTemplate.update(
                sql,
                filmId
        );
    }

    @Override
    public Collection<Film> findPopularByGenreAndYear(int count, Long genreId, Integer year) {

        String sql = """
                SELECT f.film_id
                FROM films f
                INNER JOIN genre_ref gr ON f.film_id = gr.film_id
                LEFT JOIN film_likes fl ON f.film_id = fl.film_id
                WHERE gr.genre_id = ? AND EXTRACT(YEAR FROM f.release_date) = ?
                GROUP BY f.film_id
                ORDER BY COUNT(fl.user_id) DESC, f.film_id ASC
                LIMIT ?
                """;

        List<Long> popularFilmIds = jdbcTemplate.queryForList(sql, Long.class, genreId, year, count);

        return popularFilmIds.stream()
                .map(filmId -> get(filmId).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Film> findPopularByGenre(int count, Long genreId) {

        String sql = """
                SELECT f.film_id
                FROM films f
                INNER JOIN genre_ref gr ON f.film_id = gr.film_id
                LEFT JOIN film_likes fl ON f.film_id = fl.film_id
                WHERE gr.genre_id = ?
                GROUP BY f.film_id
                ORDER BY COUNT(fl.user_id) DESC, f.film_id ASC
                LIMIT ?
                """;

        List<Long> popularFilmIds = jdbcTemplate.queryForList(sql, Long.class, genreId, count);

        return popularFilmIds.stream()
                .map(filmId -> get(filmId).orElse(null))
                .filter(Objects::nonNull) // Защита от пустых значений
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Film> findPopularByYear(int count, Integer year) {

        String sql = """
                SELECT f.film_id
                FROM films f
                INNER JOIN genre_ref gr ON f.film_id = gr.film_id
                LEFT JOIN film_likes fl ON f.film_id = fl.film_id
                WHERE EXTRACT(YEAR FROM f.release_date) = ?
                GROUP BY f.film_id
                ORDER BY COUNT(fl.user_id) DESC, f.film_id ASC
                LIMIT ?
                """;

        List<Long> popularFilmIds = jdbcTemplate.queryForList(sql, Long.class, year, count);

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

    public Collection<Film> findCommon(Long userId, Long friendId) {
        String sql = """
                SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name
                  FROM (SELECT fl_us.film_id,
                			   (SELECT COUNT(*)
                				  FROM film_likes fl
                				 WHERE film_id = fl_us.film_id) popularity
                		FROM film_likes fl_us
                		JOIN film_likes fl_fr ON fl_us.film_id = fl_fr.film_id
                		WHERE fl_us.user_id = ?
                		      AND fl_fr.user_id = ?
                	   ) s
                	   JOIN films f ON f.film_id = s.film_id
                	   LEFT JOIN mpa m ON f.mpa_id = m.mpa_id
                ORDER BY s.popularity desc
                """;

        List<Film> films = jdbcTemplate.query(sql, FILM_MAPPER, userId, friendId);

        for (Film film : films) {
            film.setGenres(genreRefStorage.findByFilmId(film.getId()));
            film.setLikeUsers(filmLikeStorage.findUserByFilmId(film.getId()));
        }
        return films;
    }

}
