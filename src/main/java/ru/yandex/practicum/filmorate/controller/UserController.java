package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private static final String EMAIL_SIMBOL = "@";
    private static final String SPACE_SIMBOL = " ";

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }


    @PostMapping
    public User create(@Valid @RequestBody User user) {
        user.setId(getNextId());

        validateUser(user);

        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {

        if (user.getId() == null) {
            log.error("Идентификатор пользователя не указан");
            throw new ValidationException("Должен быть указан id пользователя, для обновления");
        }
        if (!users.containsKey(user.getId())) {
            log.error("Не найден пользователь с id " + user.getId());
            throw new ValidationException("Не найден пользователь с id "+ user.getId());
        }

        validateUser(user);

        users.put(user.getId(), user);
        return user;
    }


    private void validateUser(User user) {

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains(EMAIL_SIMBOL)) {
            log.error("Электронная почта не может быть пустой и должна содержать символ " + EMAIL_SIMBOL);
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ " + EMAIL_SIMBOL);
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(SPACE_SIMBOL)) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя пустое, использован логин");
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

    }


    // вспомогательный метод для генерации идентификатора нового user
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
