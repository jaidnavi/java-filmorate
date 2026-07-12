package ru.yandex.practicum.filmorate.storage.dao;

import java.util.List;

public interface UserFriendDao {
    void addFriend(Long userId, Long friendUserId);

    List<Long> gerRequest(Long userId);

    void deleteFriend(Long userId, Long friendUserId);
}
