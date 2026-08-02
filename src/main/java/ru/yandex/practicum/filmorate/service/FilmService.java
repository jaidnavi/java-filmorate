package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmService {
    Collection<Film> findAll();

    Collection<Film> getByDirector(Long directorId, String sortBy);

    Film create(Film film);

    Film update(Film newFilm);

    Optional<Film> get(Long filmId);

    Film addLike(Long filmId, Long userId);

    Film deleteLike(Long filmId, Long userId);

    Collection<Film> findPopular(int count);

    Collection<Film> findPopularByGenreAndYear(int count, Long genreId, Integer year);

    Collection<Film> findPopularByGenre(int count, Long genreId);

    Collection<Film> findPopularByYear(int count, Integer year);

    void delete(Long filmId);

    Collection<Film> search(String query, List<String> by);

    Collection<Film> findCommon(Long userId, Long friendId);
}
