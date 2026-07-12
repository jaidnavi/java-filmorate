package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {
    public static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    public static final int MAX_LENGTH_DESCRIPTION = 200;

    private Long filmId;
    private Set<Long> likeUsers;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    @NotNull(message = "Рейтинг не может быть пустым")
    private Mpa mpa;

    private Set<Genre> genres;

    @AssertTrue(message = "Дата релиза — не раньше 28 декабря 1895 года")
    private boolean isReleaseDateValid() {
        if (releaseDate == null) {
            return false;
        }
        return !releaseDate.isBefore(CINEMA_BIRTHDAY);
    }
}
