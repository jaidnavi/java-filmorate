package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private int duration;

    private Set<Long> likeUsers = new LinkedHashSet<>();

    private Set<Genre> genres = new LinkedHashSet<>();

    private Set<Director> directors = new LinkedHashSet<>();

    private Mpa mpa;
}
