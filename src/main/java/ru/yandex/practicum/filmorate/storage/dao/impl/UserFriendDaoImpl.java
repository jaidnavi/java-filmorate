package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.storage.dao.UserFriendDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Component
public class UserFriendDaoImpl implements UserFriendDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserFriendDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(Long userId, Long friendUserId) {
        try {
            jdbcTemplate.update("INSERT INTO user_friends (user_id, friend_id) "
                    + "VALUES(?, ?)", userId, friendUserId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoDataFoundException("Ошибка добавления друга. Нет пользователя с id " + userId + " или " + friendUserId);
        }
    }

    @Override
    public List<Long> gerRequest(Long userId) {
        try {
            List<Long> checkFiends = jdbcTemplate.query(format("SELECT user_id, friend_id "
                            + "FROM user_friends WHERE friend_id=%d", userId), new FriendMapper())
                    .stream()
                    .map(UserFriend::getUserId)
                    .collect(Collectors.toList());
            return checkFiends;
        } catch (EmptyResultDataAccessException e) {
            throw new NoDataFoundException("Не верный id пользователя " + userId);
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendUserId) {
        try {
            jdbcTemplate.update("DELETE FROM user_friends WHERE user_id=? "
                    + "AND friend_id=?", userId, friendUserId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoDataFoundException("Не верный id пользователя ");
        }
    }

    private static class FriendMapper implements RowMapper<UserFriend> {
        @Override
        public UserFriend mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserFriend userFriend = new UserFriend();
            userFriend.setUserId(rs.getLong("user_id"));
            userFriend.setFriendId(rs.getLong("friend_id"));
            return userFriend;
        }
    }
}
