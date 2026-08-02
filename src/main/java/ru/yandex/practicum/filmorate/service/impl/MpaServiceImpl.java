package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    @Override
    public Collection<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    @Override
    public Mpa findById(Long id) {
        return mpaStorage.findById(id);
    }
}
