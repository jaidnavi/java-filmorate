package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@RestController
@AllArgsConstructor
public class UserController {

    private final UserStorage userStorage;

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
        return userStorage.addFriend(userId, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId} ")
    public User deleteFriend(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
        return userStorage.deleteFriend(userId, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> findFriends(@PathVariable("id") long userId) {
        return userStorage.findFriends(userId);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable("id") long userId, @PathVariable("otherId") long otherId) {
        return userStorage.findCommonFriends(userId, otherId);
    }

//    @ExceptionHandler
//    public Map<String, String> handle(final NoDataFoundException e) {
//        return Map.of(
//                "error", "Данные не найдены",
//                "errorMessage", e.getMessage()
//        );
//    }


}
