package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import java.util.Set;

public interface FilmDirectorStorage {

    void replaceByFilmId(Long filmId, Set<Director> directors);

    void deleteByFilmId(Long filmId);

    void addDirectorsToFilm(Set<Director> directors, Long filmId);
}
