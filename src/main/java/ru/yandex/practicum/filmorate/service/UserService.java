package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface UserService {

    User create(User user);

    User update(User newUser);

    Collection<User> findAll();

    Optional<User> get(Long userId);

    User addFriend(Long userId, Long friendUserId);

    User deleteFriend(Long userId, Long friendUserId);

    List<User> findFriends(Long userId);

    Set<User> findCommonFriends(Long userId, Long otherId);

    Collection<Film> findRecommendations(Long userId);

    void delete(Long userId);
}
