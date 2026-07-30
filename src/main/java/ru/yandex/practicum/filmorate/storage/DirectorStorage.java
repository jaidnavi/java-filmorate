package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Optional<Director> get(Long id);

    Collection<Director> findAll();

    Director create(Director director);

    Director update(Director director);

    void delete(Long directorId);
}
