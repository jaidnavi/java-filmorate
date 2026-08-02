package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Slf4j
@Service
public class GenreServiceImpl implements GenreService {
    public final GenreStorage genreStorage;
    private final GenreMapper genreMapper;

    @Autowired
    public GenreServiceImpl(GenreStorage genreStorage, GenreMapper genreMapper) {
        this.genreStorage = genreStorage;
        this.genreMapper = genreMapper;
    }

    @Override
    public Collection<GenreDTO> findAll() {
        return genreMapper.toGenreDTOCollection(genreStorage.findAll());
    }

    @Override
    public GenreDTO findById(Long id) {
        return genreMapper.toGenreDTO(genreStorage.findById(id));
    }
}
