package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;


@Slf4j
@RestController
@AllArgsConstructor
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping("/users/{id}")
    public User find(@PathVariable("id") long userId) {
        return userStorage.get(userId)
                .orElseThrow(() -> new NoDataFoundException("Пользователь с id " + userId + " не найден"));
    }

    @GetMapping("/users")
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @PostMapping("/users")
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user) {
        return userStorage.update(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> findFriends(@PathVariable("id") long userId) {
        return userService.findFriends(userId);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable("id") long userId, @PathVariable("otherId") long otherId) {
        return userService.findCommonFriends(userId, otherId);
    }

}
