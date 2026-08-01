package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.*;
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
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private static final int MAX_LENGTH_DESCRIPTION = 200;

    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность фильма должна быть указана")
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    @Builder.Default
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Set<Long> likeUsers = new LinkedHashSet<>();

    @Builder.Default
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Set<Genre> genres = new LinkedHashSet<>();

    @Builder.Default
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Set<Director> directors = new LinkedHashSet<>();

    private Mpa mpa;

    @AssertTrue(message = "Дата релиза — не раньше 28 декабря 1895 года")
    private boolean isReleaseDateValid() {
        if (releaseDate == null) {
            return false;
        }
        return !releaseDate.isBefore(CINEMA_BIRTHDAY);
    }
}
