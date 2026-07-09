package ru.yandex.practicum.filmorate.storage.dao.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@AllArgsConstructor
public class FilmDbStorage  {
    private final JdbcTemplate jdbcTemplate;
}
