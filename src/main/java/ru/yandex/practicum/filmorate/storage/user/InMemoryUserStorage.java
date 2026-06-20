package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.ServiceException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @Override
    public User update(User newUser) {
        if (users.isEmpty() || !users.containsKey(newUser.getId())) {
            log.error("Не найден пользователь с id {}", newUser.getId());
            throw new NoDataFoundException("Не найден пользователь с id " + newUser.getId());
        }
        User oldFilm = users.get(newUser.getId());
        log.info("Изменен пользователь {}.  Новое значение - {}", oldFilm.toString(), newUser);
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> get(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User addFriend(Long userId, Long friendUserId) {
        User user = get(userId).orElseThrow(() -> {
            log.error("При добавлении друзей, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При добавлении друзей, не найден пользователь с id " + userId);
        });

        User friend = get(friendUserId).orElseThrow(() -> {
            log.error("При добавлении друзей, не найден пользователь с id {}", friendUserId);
            return new NoDataFoundException("При добавлении друзей, не найден пользователь с id " + friendUserId);
        });

        Set<Long> friendsSet = user.getFriends();
        if (friendsSet != null && !friendsSet.isEmpty() && friendsSet.contains(friendUserId)) {
            log.error("Друг с id {} уже является другом пользователю с ид {}.", friendUserId, userId);
            throw new ServiceException("Пользователь с id " + friendUserId + " уже является другом пользователю с id " + userId + ".");
        } else {
            if (friendsSet == null) {
                friendsSet = new HashSet<>();
            }
            friendsSet.add(friendUserId);
        }

        // мало ли
        Set<Long> friendFriendsSet = friend.getFriends();
        if (friendFriendsSet != null && !friendFriendsSet.isEmpty() && friendFriendsSet.contains(userId)) {
            log.error("Пользователь с id {} уже является другом пользователю с ид {}.", userId, friendUserId);
            throw new ServiceException("Пользователь с id " + userId + " уже является другом пользователю с id " + friendUserId + ".");
        } else {
            if (friendFriendsSet == null) {
                friendFriendsSet = new HashSet<>();
            }
            friendFriendsSet.add(userId);
        }

        user.setFriends(friendsSet);
        update(user);
        log.info("Пользователю {} успешно добавлен новый друг {}", userId, friendUserId);

        friend.setFriends(friendFriendsSet);
        update(friend);
        log.info("Пользователю {} успешно добавлен новый друг {}", friendUserId, userId);

        return user;
    }

    @Override
    public User deleteFriend(Long userId, Long friendUserId) {
        User user = get(userId).orElseThrow(() -> {
            log.error("При удалении друзей, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При удалении друзей, не найден пользователь с id " + userId);
        });

        User friend = get(friendUserId).orElseThrow(() -> {
            log.error("При удалении друзей, не найден пользователь с id {}", friendUserId);
            return new NoDataFoundException("При удалении друзей, не найден пользователь с id " + friendUserId);
        });

        Set<Long> friendsSet = user.getFriends();
        if (friendsSet == null || friendsSet.isEmpty() || !friendsSet.contains(friendUserId)) {
            log.error("Друг с id {} уже не является другом пользователю с ид {}.", friendUserId, userId);
            throw new ServiceException("Пользователь с id " + friendUserId + " уже не является другом пользователю с id " + userId + ".");
        } else {
            friendsSet.remove(friendUserId);
        }

        // мало ли
        Set<Long> friendFriendsSet = friend.getFriends();
        if (friendFriendsSet == null || friendFriendsSet.isEmpty() || !friendFriendsSet.contains(userId)) {
            log.error("Пользователь с id {} уже не является другом пользователю с ид {}.", userId, friendUserId);
            throw new ServiceException("Пользователь с id " + userId + " уже не является другом пользователю с id " + friendUserId + ".");
        } else {
            friendFriendsSet.remove(userId);
        }

        user.setFriends(friendsSet);
        update(user);
        log.info("Пользователю {} успешно удален друг {}", userId, friendUserId);

        friend.setFriends(friendFriendsSet);
        update(friend);
        log.info("Пользователю {} успешно удален друг {}", friendUserId, userId);

        return user;
    }

    @Override
    public Collection<User> findFriends(Long userId) {

        get(userId).orElseThrow(() -> {
            log.error("При поиске друзей, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При поиске друзей, не найден пользователь с id " + userId);
        });

        return Optional.ofNullable(users.get(userId))
                .map(User::getFriends)
                .stream()
                .flatMap(Collection::stream)
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> findCommonFriends(Long userId, Long otherId) {
        Set<Long> userFriends = Optional.ofNullable(users.get(userId))
                .map(User::getFriends)
                .orElseThrow(() -> new NoDataFoundException("Пользователь с id " + userId + " не найден"));

        Set<Long> otherFriends = Optional.ofNullable(users.get(otherId))
                .map(User::getFriends)
                .orElseThrow(() -> new NoDataFoundException("Пользователь с id " + otherId + " не найден"));


        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
