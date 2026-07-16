package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface UserFriendStorage {
    void saveFriend(Long userId, Set<Long> friendsSet);

    void deleteByUserId(Long userId, Long friendId);

    void deleteAllByUserId(Long userId);

    Set<Long> findFriendIdByUserId(Long userId);
}
