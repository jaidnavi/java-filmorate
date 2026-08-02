package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;


import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class DirectorServiceImpl implements DirectorService {
    public final DirectorStorage directorStorage;

    @Autowired
    public DirectorServiceImpl(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public Collection<Director> findAll() {
        return directorStorage.findAll();
    }

    @Override
    public Optional<Director> get(Long directorId) {
        return directorStorage.get(directorId);
    }

    @Override
    public Director create(Director director) {
        return directorStorage.create(director);
    }

    @Override
    public Director update(Director newDirector) {
        return directorStorage.update(newDirector);
    }

    @Override
    public void delete(Long directorId) {
        directorStorage.delete(directorId);
    }

}
