package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.FilmDTO;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmService {
    Collection<FilmDTO> findAll();

    Collection<FilmDTO> getByDirector(Long directorId, String sortBy);

    FilmDTO create(FilmDTO film);

    FilmDTO update(FilmDTO newFilm);

    Optional<FilmDTO> get(Long filmId);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Collection<FilmDTO> findPopular(int count, Long genreId, Integer year);

    void delete(Long filmId);

    Collection<FilmDTO> search(String query, List<String> by);

    Collection<FilmDTO> findCommon(Long userId, Long friendId);
}
