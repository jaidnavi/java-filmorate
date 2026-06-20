package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User create(User user);

    User update(User user);

    Collection<User> findAll();

    Optional<User> get(Long userId);

    User addFriend(Long userId, Long friendUserId);

    User deleteFriend(Long userId, Long friendUserId);

    Collection<User> findFriends(Long userId);

    Collection<User> findCommonFriends(Long userId, Long otherId);
}
