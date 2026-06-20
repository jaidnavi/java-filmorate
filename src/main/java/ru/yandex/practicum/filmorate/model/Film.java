package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private static final int MAX_LENGTH_DESCRIPTION = 200;

    private Long id;
    private Set<Long> likeUsers;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    @AssertTrue(message = "Дата релиза — не раньше 28 декабря 1895 года")
    private boolean isReleaseDateValid() {
        if (releaseDate == null) {
            return false;
        }
        return !releaseDate.isBefore(CINEMA_BIRTHDAY);
    }
}
