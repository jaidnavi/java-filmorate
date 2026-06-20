package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;


@Slf4j
@RestController
@AllArgsConstructor
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @GetMapping("/films")
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @GetMapping("/films/{id}")
    public Film find(@PathVariable("id") long filmId) {
        return filmStorage.get(filmId)
                .orElseThrow(() -> new NoDataFoundException("Фильм с id " + filmId + " не найден"));
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
        return filmService.addLike(userId, filmId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteFriend(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        return filmService.deleteLike(userId, filmId);
    }

    @GetMapping("/films/popular")
    public Collection<Film> findPopular(
            @RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.findPopular(count);
    }

}
