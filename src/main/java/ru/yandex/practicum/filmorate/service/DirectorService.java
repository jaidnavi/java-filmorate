package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;


import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class DirectorService {
    public final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Collection<Director> findAll() {
        return directorStorage.findAll();
    }

    public Optional<Director> get(Long directorId) {
        return directorStorage.get(directorId);
    }

    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public Director update(Director newDirector) {
        return directorStorage.update(newDirector);
    }

    public void delete(Long directorId) {
        directorStorage.delete(directorId);
    }

}
