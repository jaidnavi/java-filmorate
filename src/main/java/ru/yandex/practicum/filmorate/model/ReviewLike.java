package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLike {
    private Long reviewLikeId;
    @NotNull
    private Long filmId;
    @NotNull
    private Long userId;
    @NotNull
    private Boolean isPositive;
}

