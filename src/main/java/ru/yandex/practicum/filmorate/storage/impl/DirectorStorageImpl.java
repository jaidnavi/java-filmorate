package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class DirectorStorageImpl implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final DirectorStorageImpl.DirectorMapper DIRECTOR_MAPPER = new DirectorStorageImpl.DirectorMapper();

    @Override
    public Optional<Director> get(Long id) {
        String sql = """
                SELECT f.director_id, f.name
                FROM directors f
                WHERE f.director_id = ?
                """;

        List<Director> directors = jdbcTemplate.query(sql, DIRECTOR_MAPPER, id);

        if (!directors.isEmpty()) {
            Director director = directors.get(0);
            return Optional.of(director);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Director> findAll() {
        String sql = """
                SELECT director_id, name
                FROM directors
                ORDER BY director_id
                """;

        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    @Override
    public Director create(Director director) {

        String sql = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);

        Long derectorId = keyHolder.getKey().longValue();
        log.info("Режиссер успешно создан с id = {}", derectorId);

        return get(derectorId).orElseThrow(() ->
                new NoDataFoundException("Ошибка при создании режиссера с id = " + derectorId));
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE director_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql,
                director.getName(), director.getId());

        if (rowsUpdated == 0) {
            throw new NoDataFoundException("Не найден режиссер для обновления с id = " + director.getId());
        }

        return get(director.getId()).orElseThrow(() ->
                new NoDataFoundException("Не найден режиссер с id = " + director.getId()));
    }

    @Override
    public void delete(Long directorId) {
        String sql = """
                DELETE FROM directors
                WHERE director_id = ?
                """;

        jdbcTemplate.update(
                sql,
                directorId
        );
    }

    private Director mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        Director director = new Director();
        director.setId(rs.getLong("director_id"));
        director.setName(rs.getString("name"));
        return director;
    }

    private static class DirectorMapper implements RowMapper<Director> {
        @Override
        public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
            Director director = new Director();
            director.setId(rs.getLong("director_id"));
            director.setName(rs.getString("name"));

            return director;
        }
    }

}
