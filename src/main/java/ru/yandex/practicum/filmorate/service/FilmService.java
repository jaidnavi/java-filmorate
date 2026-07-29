package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikeStorage filmLikeStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, FilmLikeStorage filmLikeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmLikeStorage = filmLikeStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public Optional<Film> get(Long filmId) {
        return filmStorage.get(filmId);
    }


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

        // 3. Возвращаем полностью обновленный и свежий объект фильма из БД для прохождения тестов
        return filmStorage.get(filmId).orElseThrow();
    }

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

        // 3. Возвращаем свежий фильм из БД
        return filmStorage.get(filmId).orElseThrow();
    }


    public Collection<Film> findPopular(int count) {
        log.info("Запрос на получение топ-{} популярных фильмов", count);
        return filmStorage.findPopular(count);
    }

    public Collection<Film> search(String query, List<String> by) {
        return filmStorage.search(query, by);
    }

}
