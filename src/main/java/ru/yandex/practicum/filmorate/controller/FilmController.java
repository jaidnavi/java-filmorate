package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private static final String DEFAULT_COUNT_POPULAR_FILMS = "10";

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDTO> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmDTO find(@PathVariable("id") long filmId) {
        return filmService.get(filmId)
                .orElseThrow(() -> new NoDataFoundException("Фильм с id " + filmId + " не найден"));
    }

    @GetMapping("/director/{id}")
    public Collection<FilmDTO> findByDirector(@PathVariable("id") long directorId,
                                           @RequestParam String sortBy) {
        return filmService.getByDirector(directorId, sortBy);
    }

    @PostMapping
    public FilmDTO create(@Valid @RequestBody FilmDTO film) {
        return filmService.create(film);
    }

    @PutMapping
    public FilmDTO update(@Valid @RequestBody FilmDTO filmDTO) {
        return filmService.update(filmDTO);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        filmService.deleteLike(filmId, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable("id") long filmId) {
        filmService.delete(filmId);
    }

    @GetMapping("/search")
    public Collection<FilmDTO> search(
            @RequestParam @NotBlank(message = "Текст для поиска не может быть пустым") String query,
            @RequestParam @NotEmpty(message = "Должно быть указано поле для поиска") List<String> by) {
        return filmService.search(query, by);
    }

    @GetMapping("/popular")
    public Collection<FilmDTO> findPopularByGenreAndYear(
            @RequestParam(defaultValue = DEFAULT_COUNT_POPULAR_FILMS)
            @Positive(message = "Параметр count должен быть больше нуля")
            int count,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer year) {
        if (genreId == null && year == null) {
            return filmService.findPopular(count);
        }
        if (year == null) {
            return filmService.findPopularByGenre(count, genreId);
        }
        if (genreId == null) {
            return filmService.findPopularByYear(count, year);
        }
        return filmService.findPopularByGenreAndYear(count, genreId, year);

    }

    /**
     * Эндпоинт возвращает список общих с другом фильмов, отсортированных по популярности.
     *
     * @param userId   Идентификатор пользователя
     * @param friendId Идентификатор друга
     * @return список фильмов
     */
    @GetMapping("/common")
    public Collection<FilmDTO> findCommon(@RequestParam long userId,
                                       @RequestParam long friendId) {
        return filmService.findCommon(userId, friendId);
    }
}
