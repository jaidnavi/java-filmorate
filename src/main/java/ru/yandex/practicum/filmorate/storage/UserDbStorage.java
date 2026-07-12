package ru.yandex.practicum.filmorate.storage;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final UserMapper USER_MAPPER = new UserMapper();

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        String sql = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );

        String selectSql = "SELECT user_id FROM users " +
                "WHERE email = ? AND login = ? AND name = ? AND birthday = ? " +
                "ORDER BY user_id DESC LIMIT 1";

        Long generatedId = jdbcTemplate.queryForObject(selectSql, Long.class,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );

        return get(generatedId).orElseThrow(() ->
                new NoDataFoundException("Ошибка при создании пользователя с id = " + generatedId));

    }

    @Override
    public User update(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getUserId()
        );

        if (rowsUpdated == 0) {
            throw new NoDataFoundException("Не найден пользователь для обновления с id = " + user.getUserId());
        }
        return get(user.getUserId()).orElseThrow(() ->
                new NoDataFoundException("Не найден пользователь с id = " + user.getUserId()));
    }

    @Override
    public Collection<User> findAll() {
        String sql = "SELECT user_id, email, login, name, birthday FROM users ORDER BY user_id";
        return jdbcTemplate.query(sql, USER_MAPPER);
    }

    @Override
    public Optional<User> get(Long userId) {
        String sql = "SELECT user_id, email, login, name, birthday FROM users WHERE user_id = ?";

        List<User> users = jdbcTemplate.query(sql, USER_MAPPER, userId);

        return users.stream().findFirst();
    }

    public static class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUserId(rs.getLong("user_id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        }
    }

}
