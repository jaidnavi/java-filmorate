package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.GenreDTO;

import java.util.Collection;

public interface GenreService {
    Collection<GenreDTO> findAll();

    GenreDTO findById(Long id);
}
