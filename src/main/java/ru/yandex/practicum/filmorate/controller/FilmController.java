package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private static final String DEFAULT_COUNT_POPULAR_FILMS = "10";

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film find(@PathVariable("id") long filmId) {
        return filmService.get(filmId)
                .orElseThrow(() -> new NoDataFoundException("Фильм с id " + filmId + " не найден"));
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteFriend(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> findPopular(
            @RequestParam(defaultValue = DEFAULT_COUNT_POPULAR_FILMS, required = false)
            @Positive(message = "Параметр count должен быть больше нуля")
            Integer count) {
        return filmService.findPopular(count);
    }

}
