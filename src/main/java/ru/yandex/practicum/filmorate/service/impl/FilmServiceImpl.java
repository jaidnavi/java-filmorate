package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage, UserStorage userStorage, FilmLikeStorage filmLikeStorage, DirectorStorage directorStorage, EventsService eventsService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmLikeStorage = filmLikeStorage;
        this.directorStorage = directorStorage;
        this.eventsService = eventsService;
    }

    @Override
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Collection<Film> getByDirector(Long directorId, String sortBy) {
        directorStorage.get(directorId)
                .orElseThrow(() -> new NoDataFoundException("Режиссер с id " + directorId + " не найден"));

        return filmStorage.getByDirector(directorId, sortBy);
    }

    @Override
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    @Override
    public Optional<Film> get(Long filmId) {
        return filmStorage.get(filmId);
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        // 1. Проверяем существование фильма и пользователя (иначе честная 404)
        filmStorage.get(filmId).orElseThrow(() -> {
            log.error("При добавлении лайка, не найден фильм с id {}", filmId);
            return new NoDataFoundException("При добавлении лайка, не найден фильм с id " + filmId);
        });

        userStorage.get(userId).orElseThrow(() -> {
            log.error("При добавлении лайка, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При добавлении лайка, не найден пользователь с id " + userId);
        });

        // 2. Напрямую вызываем метод добавления лайка в БД через хранилище
        filmLikeStorage.add(filmId, userId);
        log.info("Фильму с id {} успешно добавлен лайк от пользователя с id {}", filmId, userId);

        // 3. Фиксируем событие в ленту
        eventsService.addNewEvent(userId, EventType.LIKE, filmId, OperationType.ADD);

        // 4. Возвращаем полностью обновленный и свежий объект фильма из БД для прохождения тестов
        return filmStorage.get(filmId).orElseThrow();
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        // 1. Проверяем существование фильма и пользователя
        filmStorage.get(filmId).orElseThrow(() -> {
            log.error("При удалении лайка, не найден фильм с id {}", filmId);
            return new NoDataFoundException("При удалении лайка, не найден фильм с id " + filmId);
        });

        userStorage.get(userId).orElseThrow(() -> {
            log.error("При удалении лайка, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При удалении лайка, не найден пользователь с id " + userId);
        });

        // 2. Напрямую вызываем удаление строки из таблицы film_likes
        filmLikeStorage.delete(filmId, userId);
        log.info("У фильма с id {} успешно удален лайк от пользователя с id {}", filmId, userId);

        // 3. Фиксируем событие в ленту
        eventsService.addNewEvent(userId, EventType.LIKE, filmId, OperationType.REMOVE);

        // 4. Возвращаем свежий фильм из БД
        return filmStorage.get(filmId).orElseThrow();
    }

    @Override
    public Collection<Film> findPopular(int count) {
        log.info("Запрос на получение топ-{} популярных фильмов", count);
        return filmStorage.findPopular(count);
    }

    @Override
    public Collection<Film> findPopularByGenreAndYear(int count, Long genreId, Integer year) {
        return filmStorage.findPopularByGenreAndYear(count, genreId, year);
    }

    @Override
    public Collection<Film> findPopularByGenre(int count, Long genreId) {
        return filmStorage.findPopularByGenre(count, genreId);
    }

    @Override
    public Collection<Film> findPopularByYear(int count, Integer year) {
        return filmStorage.findPopularByYear(count, year);
    }

    @Override
    public void delete(Long filmId) {
        filmStorage.delete(filmId);
    }

    @Override
    public Collection<Film> search(String query, List<String> by) {
        return filmStorage.search(query, by);
    }

    @Override
    public Collection<Film> findCommon(Long userId, Long friendId) {
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
        return filmStorage.findCommon(userId, friendId);
    }

}
