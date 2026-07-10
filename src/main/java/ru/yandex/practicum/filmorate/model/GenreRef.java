package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreRef {
    @NotNull
    private Long genreRefId;
    @NotNull
    private Long genreId;
    @NotNull
    private Long filmId;
}
