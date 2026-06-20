package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Map;


@Slf4j
@RestController
@AllArgsConstructor
public class FilmController {

    private final FilmStorage filmStorage;

    @GetMapping("/films")
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) {
        return filmStorage.create(film);
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        return filmStorage.update(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film likeFilm(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        return filmStorage.addLike(userId, filmId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteFriend(@PathVariable("id") long filmId, @PathVariable("friendId") long userId) {
        return filmStorage.deleteLike(userId, filmId);
    }

    @GetMapping("/films/popular")
    public Collection<Film> findPopular(
            @RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmStorage.findPopular(count);
    }

//    @ExceptionHandler
//    public Map<String, String> handle(final NoDataFoundException e) {
//        return Map.of(
//                "error", "Данные не найдены",
//                "errorMessage", e.getMessage()
//        );
//    }

}
