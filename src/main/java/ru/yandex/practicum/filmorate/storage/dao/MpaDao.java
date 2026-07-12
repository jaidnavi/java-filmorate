package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaDao {
    Mpa getMpaById(Long mpaId);

    List<Mpa> getAllMpa();
}
