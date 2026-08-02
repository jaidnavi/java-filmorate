package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDTO;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;


import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class DirectorServiceImpl implements DirectorService {
    public final DirectorStorage directorStorage;
    private final DirectorMapper directorMapper;

    @Autowired
    public DirectorServiceImpl(DirectorStorage directorStorage, DirectorMapper directorMapper) {
        this.directorStorage = directorStorage;
        this.directorMapper = directorMapper;
    }

    @Override
    public Collection<DirectorDTO> findAll() {
        return directorMapper.toDirectorDTOCollection(directorStorage.findAll());
    }

    @Override
    public Optional<DirectorDTO> get(Long directorId) {
        return directorStorage.get(directorId).map(directorMapper::toDirectorDTO);
    }

    @Override
    public DirectorDTO create(DirectorDTO directorDTO) {
        Director director = directorMapper.toDirector(directorDTO);
        return directorMapper.toDirectorDTO(directorStorage.create(director));
    }

    @Override
    public DirectorDTO update(DirectorDTO directorDTO) {
        Director director = directorMapper.toDirector(directorDTO);
        return directorMapper.toDirectorDTO(directorStorage.update(director));
    }

    @Override
    public void delete(Long directorId) {
        directorStorage.delete(directorId);
    }

}
