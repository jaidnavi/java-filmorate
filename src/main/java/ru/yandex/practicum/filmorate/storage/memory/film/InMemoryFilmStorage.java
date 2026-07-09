package ru.yandex.practicum.filmorate.storage.memory.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
@AllArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (films.isEmpty() || !films.containsKey(newFilm.getId())) {
            log.error("Не найден фильм с id {} ", newFilm.getId());
            throw new NoDataFoundException("Не найден фильм с id " + newFilm.getId());
        }
        Film oldFilm = films.get(newFilm.getId());
        log.info("Изменен фильм {}. Новое значение {}.", oldFilm.toString(), newFilm);
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Optional<Film> get(Long filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

}
