package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDTO;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public DirectorDTO create(@Valid @RequestBody DirectorDTO directorDTO) {
        return directorService.create(directorDTO);
    }

    @GetMapping
    public Collection<DirectorDTO> findAll() {
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public DirectorDTO find(@PathVariable("id") long directorId) {
        return directorService.get(directorId)
                .orElseThrow(() -> new NoDataFoundException("Режиссер с id " + directorId + " не найден"));
    }

    @PutMapping
    public DirectorDTO update(@Valid @RequestBody DirectorDTO directorDTO) {
        return directorService.update(directorDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") long directorId) {
        directorService.delete(directorId);
    }

}
