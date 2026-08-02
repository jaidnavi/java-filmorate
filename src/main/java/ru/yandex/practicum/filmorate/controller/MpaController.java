package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Validated
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public Collection<MpaDTO> allMpa() {
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public MpaDTO getMpaId(@PathVariable Long id) {
        return mpaService.findById(id);
    }
}
