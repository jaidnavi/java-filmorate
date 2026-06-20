package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.ServiceException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final UserStorage userStorage;


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

    @Override
    public Film addLike(Long userId, Long filmId) {
        User user = userStorage.get(userId).orElseThrow(() -> {
            log.error("При добавлении лайка, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При добавлении лайка, не найден пользователь с id " + userId);
        });

        Film film = get(filmId).orElseThrow(() -> {
            log.error("При добавлении лайка, не найден фильм с id {}", filmId);
            return new NoDataFoundException("При добавлении лайка, не найден фильм с id " + filmId);
        });

        Set<Long> likeFilmsSet = user.getLikeFilms();
        if (likeFilmsSet != null && !likeFilmsSet.isEmpty() && likeFilmsSet.contains(filmId)) {
            log.error("Фильм с id {} уже отмечен лайком пользователем с id {}.", filmId, userId);
            throw new ServiceException("Фильм с id " + filmId + " уже отмечен лайком пользователем с id " + userId + ".");
        } else {

            if (likeFilmsSet == null) {
                likeFilmsSet = new HashSet<>();
            }
            likeFilmsSet.add(filmId);
        }

        Set<Long> likeUsersSet = film.getLikeUsers();
        if (likeUsersSet != null && !likeUsersSet.isEmpty() && likeUsersSet.contains(userId)) {
            log.error("Пользователь с id {} уже отметил лайком фильм с id {}.", userId, filmId);
            throw new ServiceException("Пользователь с id " + userId + " уже отметил лайком фильм с id " + filmId + ".");
        } else {
            if (likeUsersSet == null) {
                likeUsersSet = new HashSet<>();
            }
            likeUsersSet.add(userId);
        }

        user.setLikeFilms(likeFilmsSet);
        userStorage.update(user);
        log.info("Пользователь с id {} успешно лайкнул фильм с id {}", userId, filmId);

        film.setLikeUsers(likeUsersSet);
        update(film);
        log.info("Фильму с id {} успешно добавлен лайк от пользователя с id {}", filmId, userId);

        return film;
    }

    @Override
    public Film deleteLike(Long userId, Long filmId) {
        User user = userStorage.get(userId).orElseThrow(() -> {
            log.error("При удалении лайка, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При удалении лайка, не найден пользователь с id " + userId);
        });

        Film film = get(filmId).orElseThrow(() -> {
            log.error("При удалении лайка, не найден фильм с id {}", filmId);
            return new NoDataFoundException("При удалении лайка, не найден фильм с id " + filmId);
        });

        Set<Long> likeFilmsSet = user.getLikeFilms();
        if (likeFilmsSet == null || likeFilmsSet.isEmpty() || !likeFilmsSet.contains(filmId)) {
            log.error("Фильм с id {} не отмечен лайком пользователем с id {}.", filmId, userId);
            throw new ServiceException("Фильм с id " + filmId + " не отмечен лайком пользователем с id " + userId + ".");
        } else {
            likeFilmsSet.remove(filmId);
        }

        Set<Long> likeUsersSet = film.getLikeUsers();
        if (likeUsersSet == null || likeUsersSet.isEmpty() || !likeUsersSet.contains(userId)) {
            log.error("Пользователь с id {} не отметил лайком фильм с id {}.", userId, filmId);
            throw new ServiceException("Пользователь с id " + userId + " не отметил лайком фильм с id " + filmId + ".");
        } else {
            likeUsersSet.remove(userId);
        }

        user.setLikeFilms(likeFilmsSet);
        userStorage.update(user);
        log.info("Пользователь с id {} успешно удалил из понравившихся фильм с id {}", userId, filmId);

        film.setLikeUsers(likeUsersSet);
        update(film);
        log.info("У фильма с id {} успешно удален лайк от пользователя с id {}", filmId, userId);

        return film;
    }

    @Override
    public Collection<Film> findPopular(Integer count) {
        return films.values().stream()
                .sorted((f1, f2) -> {
                    int likes1 = (f1.getLikeUsers() == null) ? 0 : f1.getLikeUsers().size();
                    int likes2 = (f2.getLikeUsers() == null) ? 0 : f2.getLikeUsers().size();
                    return Integer.compare(likes2, likes1);
                })
                .limit(count)
                .collect(Collectors.toList());
    }

}
