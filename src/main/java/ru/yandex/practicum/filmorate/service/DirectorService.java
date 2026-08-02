package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorService {

    Collection<Director> findAll();

    Optional<Director> get(Long directorId);

    Director create(Director director);

    Director update(Director newDirector);

    void delete(Long directorId);

}
