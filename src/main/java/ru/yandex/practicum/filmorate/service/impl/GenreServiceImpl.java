package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    public final GenreStorage genreStorage;
    private final GenreMapper genreMapper;

    @Override
    public Collection<GenreDTO> findAll() {
        return genreMapper.toGenreDTOCollection(genreStorage.findAll());
    }

    @Override
    public GenreDTO findById(Long id) {
        return genreMapper.toGenreDTO(genreStorage.findById(id));
    }
}
