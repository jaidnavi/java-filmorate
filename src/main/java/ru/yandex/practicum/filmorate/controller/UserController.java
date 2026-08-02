package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Events;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Set;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDTO find(@PathVariable("id") long userId) {
        return userService.get(userId)
                .orElseThrow(() -> new NoDataFoundException("Пользователь с id " + userId + " не найден"));
    }

    @GetMapping
    public Collection<UserDTO> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public UserDTO create(@Valid @RequestBody UserDTO userDTO) {
        return userService.create(userDTO);
    }

    @PutMapping
    public UserDTO update(@Valid @RequestBody UserDTO userDTO) {
        return userService.update(userDTO);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public UserDTO addFriend(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDTO> findFriends(@PathVariable("id") long userId) {
        return userService.findFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<UserDTO> findCommonFriends(@PathVariable("id") long userId, @PathVariable("otherId") long otherId) {
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
        return userService.getFeedByUserId(userId);
    }

    /**
     * Эндпоинт возвращает всю ленту событий
     *
     * @return список событий
     */
    @GetMapping("/feed")
    public Collection<Events> getAllFeed() {
        return userService.getAllFeed();
    }

    @GetMapping("/{id}/recommendations")
    public Collection<FilmDTO> findCommonFriends(@PathVariable("id") long userId) {
        return userService.findRecommendations(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") long userId) {
        userService.delete(userId);
    }

}
