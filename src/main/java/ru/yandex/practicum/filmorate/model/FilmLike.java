package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmLike {
    @NotNull
    private Long filmLikeId;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
}
