package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotations.MinDate;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilmDTO {
    /**
     * Дата самого раннего фильма
     */
    public static final String MIN_RELEASE_DATE = "28.12.1895";
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private Long id;
    @NotNull(message = "Название фильма не может быть пустым")
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Длина описания должна быть максимум 200 символов")
    private String description;
    @MinDate(minDate = MIN_RELEASE_DATE,message = "Дата релиза должна быть не ранее " + MIN_RELEASE_DATE)
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
    private Mpa mpa;
    @Builder.Default
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Set<Long> likeUsers = new LinkedHashSet<>();
    @Builder.Default
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Set<Genre> genres = new LinkedHashSet<>();
    @Builder.Default
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Set<Director> directors = new LinkedHashSet<>();
}
