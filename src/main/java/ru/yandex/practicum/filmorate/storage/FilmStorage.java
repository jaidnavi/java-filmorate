package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Collection<Film> getByDirector(Long directorId, String sortBy);

    Optional<Film> get(Long filmId);

    Collection<Film> findPopular(int count);

    /** Метод возвращает список общих с другом фильмов, отсортированных по популярности.
     * @param userId Идентификатор пользователя
     * @param friendId Идентификатор друга
     * @return список фильмов
     */
    Collection<Film> findCommon(Long userId, Long friendId);

    Collection<Film> findRecommendations(Long userId);

    Collection<Film> search(String query, List<String> by);

    void delete(Long filmId);

    Collection<Film> findPopularByGenreAndYear(int count, Long genreId, Integer year);

    Collection<Film> findPopularByGenre(int count, Long genreId);

    Collection<Film> findPopularByYear(int count, Integer year);
}