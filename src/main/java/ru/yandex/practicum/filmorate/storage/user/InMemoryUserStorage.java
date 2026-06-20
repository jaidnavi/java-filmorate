package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Optional;


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

}
