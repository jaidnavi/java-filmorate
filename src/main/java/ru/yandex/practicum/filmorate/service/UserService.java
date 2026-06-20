package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(Long userId, Long friendUserId) {
        User user = userStorage.get(userId).orElseThrow(() -> {
            log.error("При добавлении друзей, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При добавлении друзей, не найден пользователь с id " + userId);
        });

        User friend = userStorage.get(friendUserId).orElseThrow(() -> {
            log.error("При добавлении друзей, не найден пользователь с id {}", friendUserId);
            return new NoDataFoundException("При добавлении друзей, не найден пользователь с id " + friendUserId);
        });

        Set<Long> friendsSet = user.getFriends();
        if (friendsSet != null && !friendsSet.isEmpty() && friendsSet.contains(friendUserId)) {
            log.info("Друг с id {} уже является другом пользователю с ид {}.", friendUserId, userId);
        } else {
            if (friendsSet == null) {
                friendsSet = new HashSet<>();
            }
            friendsSet.add(friendUserId);
        }

        Set<Long> friendFriendsSet = friend.getFriends();
        if (friendFriendsSet != null && !friendFriendsSet.isEmpty() && friendFriendsSet.contains(userId)) {
            log.info("Пользователь с id {} уже является другом пользователю с ид {}.", userId, friendUserId);
        } else {
            if (friendFriendsSet == null) {
                friendFriendsSet = new HashSet<>();
            }
            friendFriendsSet.add(userId);
        }

        user.setFriends(friendsSet);
        userStorage.update(user);
        log.info("Пользователю {} успешно добавлен новый друг {}", userId, friendUserId);

        friend.setFriends(friendFriendsSet);
        userStorage.update(friend);
        log.info("Пользователю {} успешно добавлен новый друг {}", friendUserId, userId);

        return user;
    }

    public User deleteFriend(Long userId, Long friendUserId) {
        User user = userStorage.get(userId).orElseThrow(() -> {
            log.error("При удалении друзей, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При удалении друзей, не найден пользователь с id " + userId);
        });

        User friend = userStorage.get(friendUserId).orElseThrow(() -> {
            log.error("При удалении друзей, не найден пользователь с id {}", friendUserId);
            return new NoDataFoundException("При удалении друзей, не найден пользователь с id " + friendUserId);
        });

        Set<Long> friendsSet = user.getFriends();
        if (friendsSet == null || friendsSet.isEmpty() || !friendsSet.contains(friendUserId)) {
            log.info("Друг с id {} уже не является другом пользователю с ид {}.", friendUserId, userId);
        } else {
            friendsSet.remove(friendUserId);
        }

        Set<Long> friendFriendsSet = friend.getFriends();
        if (friendFriendsSet == null || friendFriendsSet.isEmpty() || !friendFriendsSet.contains(userId)) {
            log.info("Пользователь с id {} уже не является другом пользователю с ид {}.", userId, friendUserId);
        } else {
            friendFriendsSet.remove(userId);
        }

        user.setFriends(friendsSet);
        userStorage.update(user);
        log.info("Пользователю {} успешно удален друг {}", userId, friendUserId);

        friend.setFriends(friendFriendsSet);
        userStorage.update(friend);
        log.info("Пользователю {} успешно удален друг {}", friendUserId, userId);

        return user;
    }


    public Collection<User> findFriends(Long userId) {

        User user = userStorage.get(userId).orElseThrow(() -> {
            log.error("При поиске друзей, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При поиске друзей, не найден пользователь с id " + userId);
        });

        return Optional.ofNullable(user.getFriends())
                .stream()
                .flatMap(Collection::stream)
                .map(userStorage::get)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    public Collection<User> findCommonFriends(Long userId, Long otherId) {
        Set<Long> userFriends = userStorage.get(userId)
                .map(User::getFriends)
                .orElseThrow(() -> new NoDataFoundException("Пользователь с id " + userId + " не найден"));

        Set<Long> otherFriends = userStorage.get(otherId)
                .map(User::getFriends)
                .orElseThrow(() -> new NoDataFoundException("Пользователь с id " + otherId + " не найден"));


        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(userStorage::get)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

}
