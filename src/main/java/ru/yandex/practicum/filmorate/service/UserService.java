package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserFriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final UserFriendStorage userFriendStorage;

    @Autowired
    public UserService(UserStorage userStorage, UserFriendStorage userFriendStorage) {
        this.userStorage = userStorage;
        this.userFriendStorage = userFriendStorage;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public Optional<User> get(Long userId) {
        return userStorage.get(userId);
    }

    public User addFriend(Long userId, Long friendUserId) {
        User user = userStorage.get(userId).orElseThrow(() -> {
            log.error("При добавлении друзей, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При добавлении друзей, не найден пользователь с id " + userId);
        });

        userStorage.get(friendUserId).orElseThrow(() -> {
            log.error("При добавлении друзей, не найден пользователь с id {}", friendUserId);
            return new NoDataFoundException("При добавлении друзей, не найден пользователь с id " + friendUserId);
        });

        Set<Long> currentFriends = userFriendStorage.findFriendIdByUserId(userId);
        Set<Long> friendsSet = new HashSet<>(currentFriends != null ? currentFriends : Collections.emptySet());

        if (friendsSet.contains(friendUserId)) {
            log.info("Друг с id {} уже является другом пользователю с ид {}.", friendUserId, userId);
        } else {
            friendsSet.add(friendUserId);
            userFriendStorage.saveFriend(userId, friendsSet);
            log.info("Пользователю {} успешно добавлен новый друг {}", userId, friendUserId);
        }

        return userStorage.get(userId).orElseThrow(() -> {
            log.error("Ошибка при получении обновленного пользователя с id {}", userId);
            return new NoDataFoundException("Пользователь с id " + userId + " не найден после обновления");
        });
    }

    public User deleteFriend(Long userId, Long friendUserId) {

        User user = userStorage.get(userId).orElseThrow(() -> {
            log.error("При удалении друзей, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При удалении друзей, не найден пользователь с id " + userId);
        });

        userStorage.get(friendUserId).orElseThrow(() -> {
            log.error("При удалении друзей, не найден пользователь с id {}", friendUserId);
            return new NoDataFoundException("При удалении друзей, не найден пользователь с id " + friendUserId);
        });

        userFriendStorage.deleteByUserId(userId, friendUserId);
        log.info("Пользователю {} успешно удален друг {}", userId, friendUserId);

        return userStorage.get(userId).orElseThrow(() ->
                new NoDataFoundException("Пользователь не найден после удаления друга"));
    }

    public Set<User> findFriends(Long userId) {

        User user = userStorage.get(userId).orElseThrow(() -> {
            log.error("При поиске друзей, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При поиске друзей, не найден пользователь с id " + userId);
        });

        if (user.getFriends() == null) {
            return Collections.emptySet();
        }

        return user.getFriends().stream()
                .map(userStorage::get)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public Set<User> findCommonFriends(Long userId, Long otherId) {
        Set<Long> userFriends = userStorage.get(userId)
                .map(User::getFriends)
                .orElseThrow(() -> new NoDataFoundException("Пользователь с id " + userId + " не найден"));

        Set<Long> otherFriends = userStorage.get(otherId)
                .map(User::getFriends)
                .orElseThrow(() -> new NoDataFoundException("Пользователь с id " + otherId + " не найден"));

        if (userFriends == null || otherFriends == null) {
            return Collections.emptySet();
        }

        Set<Long> commonIds = userFriends.stream()
                .filter(otherFriends::contains)
                .collect(Collectors.toSet());

        if (commonIds.isEmpty()) {
            return Collections.emptySet();
        }

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(userStorage::get)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

}
