package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Service
@Component
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaById(Long mpaId) {
        try {
            return jdbcTemplate.queryForObject(format("SELECT * FROM mpa WHERE mpa_id=%d", mpaId), new MpaMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NoDataFoundException("Не найден рейтинг с id " + mpaId);
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        return new ArrayList<>(jdbcTemplate.query("SELECT * FROM mpa ORDER BY mpa_id", new MpaMapper()));
    }

    private static class MpaMapper implements RowMapper<Mpa> {
        @Override
        public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
            Mpa map = new Mpa();
            map.setMpaId(rs.getLong("mpa_id"));
            map.setName(rs.getString("name"));
            return map;
        }
    }
}
