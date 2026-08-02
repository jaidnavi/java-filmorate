package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.MpaDTO;

import java.util.Collection;

public interface MpaService {

    Collection<MpaDTO> findAll();

    MpaDTO findById(Long id);
}
