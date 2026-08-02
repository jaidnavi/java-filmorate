package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.DirectorDTO;

import java.util.Collection;
import java.util.Optional;

public interface DirectorService {

    Collection<DirectorDTO> findAll();

    Optional<DirectorDTO> get(Long directorId);

    DirectorDTO create(DirectorDTO directorDTO);

    DirectorDTO update(DirectorDTO directorDTO);

    void delete(Long directorId);

}
