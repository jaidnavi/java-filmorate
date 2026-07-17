package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.storage.UserFriendStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserFriendStorageImpl implements UserFriendStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveFriend(Long userId, Set<Long> friendsSet) {

        deleteAllByUserId(userId);

        if (friendsSet == null || friendsSet.isEmpty()) {
            return;
        }

        String sql = """
                MERGE INTO user_friends (user_id, friend_id)
                KEY (user_id, friend_id)
                VALUES (?, ?)
                """;

        for (Long friendId : friendsSet) {
            jdbcTemplate.update(sql, userId, friendId);
        }

    }

    @Override
    public void deleteByUserId(Long userId, Long friendId) {

        String sql = """
                DELETE FROM user_friends
                WHERE user_id = ?
                AND friend_id = ?
                """;

        int rowsAffected = jdbcTemplate.update(
                sql,
                userId,
                friendId
        );

    }

    @Override
    public void deleteAllByUserId(Long userId) {
        Objects.requireNonNull(userId, "Id пользователя должен быть указан");

        String sql = """
                DELETE FROM user_friends
                WHERE user_id = ?
                """;

        jdbcTemplate.update(
                sql,
                userId
        );
    }


    @Override
    public Set<Long> findFriendIdByUserId(Long userId) {
        Objects.requireNonNull(userId, "Id пользователя должен быть указан");

        String sql = """
                SELECT friend_id
                FROM user_friends
                WHERE user_id = ? AND friend_id IS NOT NULL
                """;

        List<Long> friendIds = jdbcTemplate.queryForList(sql, Long.class, userId);

        return Set.copyOf(friendIds);
    }

    private UserFriend mapRowToUserFriend(ResultSet rs, int rowNum) throws SQLException {
        UserFriend userFriend = new UserFriend();

        userFriend.setUserId(rs.getLong("user_id"));
        userFriend.setFriendId(rs.getLong("friend_id"));

        return userFriend;
    }

}
