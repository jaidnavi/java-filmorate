package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.service.EventsService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikeStorage filmLikeStorage;
    private final DirectorStorage directorStorage;
    private final EventsService eventsService;
    private final FilmMapper filmMapper;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage, UserStorage userStorage, FilmLikeStorage filmLikeStorage, DirectorStorage directorStorage, EventsService eventsService, FilmMapper filmMapper) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmLikeStorage = filmLikeStorage;
        this.directorStorage = directorStorage;
        this.eventsService = eventsService;
        this.filmMapper = filmMapper;
    }

    @Override
    public Collection<FilmDTO> findAll() {
        return filmMapper.toFilmDTOCollection(filmStorage.findAll());
    }

    @Override
    public Collection<FilmDTO> getByDirector(Long directorId, String sortBy) {
        directorStorage.get(directorId)
                .orElseThrow(() -> new NoDataFoundException("Режиссер с id " + directorId + " не найден"));

        return filmMapper.toFilmDTOCollection(filmStorage.getByDirector(directorId, sortBy).stream().toList());
    }

    @Override
    public FilmDTO create(FilmDTO filmDTO) {
        Film film = filmMapper.toFilm(filmDTO);
        return filmMapper.toFilmDTO(filmStorage.create(film));
    }

    @Override
    public FilmDTO update(FilmDTO newFilm) {
        Film film = filmMapper.toFilm(newFilm);
        return filmMapper.toFilmDTO(filmStorage.update(film));
    }

    @Override
    public Optional<FilmDTO> get(Long filmId) {
        return filmStorage.get(filmId).map(filmMapper::toFilmDTO);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        filmStorage.get(filmId).orElseThrow(() -> {
            log.error("При добавлении лайка, не найден фильм с id {}", filmId);
            return new NoDataFoundException("При добавлении лайка, не найден фильм с id " + filmId);
        });

        userStorage.get(userId).orElseThrow(() -> {
            log.error("При добавлении лайка, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При добавлении лайка, не найден пользователь с id " + userId);
        });

        filmLikeStorage.add(filmId, userId);
        log.info("Фильму с id {} успешно добавлен лайк от пользователя с id {}", filmId, userId);

        eventsService.addNewEvent(userId, EventType.LIKE, filmId, OperationType.ADD);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        filmStorage.get(filmId).orElseThrow(() -> {
            log.error("При удалении лайка, не найден фильм с id {}", filmId);
            return new NoDataFoundException("При удалении лайка, не найден фильм с id " + filmId);
        });

        userStorage.get(userId).orElseThrow(() -> {
            log.error("При удалении лайка, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При удалении лайка, не найден пользователь с id " + userId);
        });

        filmLikeStorage.delete(filmId, userId);
        log.info("У фильма с id {} успешно удален лайк от пользователя с id {}", filmId, userId);

        eventsService.addNewEvent(userId, EventType.LIKE, filmId, OperationType.REMOVE);
    }

    @Override
    public Collection<FilmDTO> findPopular(int count) {
        log.info("Запрос на получение топ-{} популярных фильмов", count);
        return filmMapper.toFilmDTOCollection(filmStorage.findPopular(count));
    }

    @Override
    public Collection<FilmDTO> findPopularByGenreAndYear(int count, Long genreId, Integer year) {
        return filmMapper.toFilmDTOCollection(filmStorage.findPopularByGenreAndYear(count, genreId, year));
    }

    @Override
    public Collection<FilmDTO> findPopularByGenre(int count, Long genreId) {
        return filmMapper.toFilmDTOCollection(filmStorage.findPopularByGenre(count, genreId));
    }

    @Override
    public Collection<FilmDTO> findPopularByYear(int count, Integer year) {
        return filmMapper.toFilmDTOCollection(filmStorage.findPopularByYear(count, year));
    }

    @Override
    public void delete(Long filmId) {
        filmStorage.delete(filmId);
    }

    @Override
    public Collection<FilmDTO> search(String query, List<String> by) {
        return filmMapper.toFilmDTOCollection(filmStorage.search(query, by));
    }

    @Override
    public Collection<FilmDTO> findCommon(Long userId, Long friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new ValidationException("Идентификатор пользователя и друга совпадают. Это недопустимо");
        }
        userStorage.get(userId).orElseThrow(() -> {
            log.error("При поиске общих фильмов, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При поиске общих фильмов, не найден пользователь с id " + userId);
        });
        userStorage.get(friendId).orElseThrow(() -> {
            log.error("При поиске общих фильмов, не найден друг с id {}", friendId);
            return new NoDataFoundException("При поиске общих фильмов, не найден друг с id " + friendId);
        });
        log.info("Запрос на получение общих фильмов между пользователем с userId={} и его другом c friendId={}", userId, friendId);
        return filmMapper.toFilmDTOCollection(filmStorage.findCommon(userId, friendId));
    }

}
