package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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


    public Film addLike(Long userId, Long filmId) {
        User user = userStorage.get(userId).orElseThrow(() -> {
            log.error("При добавлении лайка, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При добавлении лайка, не найден пользователь с id " + userId);
        });

        Film film = filmStorage.get(filmId).orElseThrow(() -> {
            log.error("При добавлении лайка, не найден фильм с id {}", filmId);
            return new NoDataFoundException("При добавлении лайка, не найден фильм с id " + filmId);
        });

        Set<Long> likeUsersSet = film.getLikeUsers();
        if (likeUsersSet != null && !likeUsersSet.isEmpty() && likeUsersSet.contains(userId)) {
            log.info("Пользователь с id {} уже отметил лайком фильм с id {}.", userId, filmId);
        } else {
            if (likeUsersSet == null) {
                likeUsersSet = new HashSet<>();
            }
            likeUsersSet.add(userId);
        }

        film.setLikeUsers(likeUsersSet);
        filmStorage.update(film);
        log.info("Фильму с id {} успешно добавлен лайк от пользователя с id {}", filmId, userId);

        return film;
    }

    public Film deleteLike(Long userId, Long filmId) {
        User user = userStorage.get(userId).orElseThrow(() -> {
            log.error("При удалении лайка, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При удалении лайка, не найден пользователь с id " + userId);
        });

        Film film = filmStorage.get(filmId).orElseThrow(() -> {
            log.error("При удалении лайка, не найден фильм с id {}", filmId);
            return new NoDataFoundException("При удалении лайка, не найден фильм с id " + filmId);
        });


        Set<Long> likeUsersSet = film.getLikeUsers();
        if (likeUsersSet == null || likeUsersSet.isEmpty() || !likeUsersSet.contains(userId)) {
            log.info("Пользователь с id {} не отметил лайком фильм с id {}.", userId, filmId);
        } else {
            likeUsersSet.remove(userId);
        }

        film.setLikeUsers(likeUsersSet);
        filmStorage.update(film);
        log.info("У фильма с id {} успешно удален лайк от пользователя с id {}", filmId, userId);

        return film;
    }

    public Collection<Film> findPopular(Integer count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> {
                    int likes1 = (f1.getLikeUsers() == null) ? 0 : f1.getLikeUsers().size();
                    int likes2 = (f2.getLikeUsers() == null) ? 0 : f2.getLikeUsers().size();
                    return Integer.compare(likes2, likes1);
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}
