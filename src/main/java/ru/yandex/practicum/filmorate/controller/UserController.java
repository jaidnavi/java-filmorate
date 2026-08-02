package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Events;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventsService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Set;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final EventsService eventsService;

    @GetMapping("/{id}")
    public User find(@PathVariable("id") long userId) {
        return userService.get(userId)
                .orElseThrow(() -> new NoDataFoundException("Пользователь с id " + userId + " не найден"));
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
        User user = userService.addFriend(userId, friendId);
        return user;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findFriends(@PathVariable("id") long userId) {
        return userService.findFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> findCommonFriends(@PathVariable("id") long userId, @PathVariable("otherId") long otherId) {
        return userService.findCommonFriends(userId, otherId);
    }

    /**
     * Эндпоинт возвращает ленту событий пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список событий
     */
    @GetMapping("/{id}/feed")
    public Collection<Events> getFeedByUserId(@PathVariable("id") long userId) {
        return eventsService.getFeedByUserId(userId);
    }

    /**
     * Эндпоинт возвращает всю ленту событий
     *
     * @return список событий
     */
    @GetMapping("/feed")
    public Collection<Events> getAllFeed() {
        return eventsService.getAllFeed();
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> findCommonFriends(@PathVariable("id") long userId) {
        return userService.findRecommendations(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") long userId) {
        userService.delete(userId);
    }

}
