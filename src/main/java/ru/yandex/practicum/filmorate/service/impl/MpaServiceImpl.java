package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;
    private final MpaMapper mpaMapper;

    @Override
    public Collection<MpaDTO> findAll() {
        return mpaMapper.toMpaDTOCollection(mpaStorage.findAll());
    }

    @Override
    public MpaDTO findById(Long id) {
        return mpaMapper.toMpaDTO(mpaStorage.findById(id));
    }
}
