package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldValidateCorrectFilm() {
        Film film = Film.builder()
                .name("Inception")
                .description("A thief who steals corporate secrets...")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .mpa(Mpa.builder()
                        .name("   ")
                        .build())
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        Film film = Film.builder()
                .name("   ")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .mpa(Mpa.builder()
                        .name("   ")
                        .build())
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
        assertEquals("Название фильма не может быть пустым",
                violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenDescriptionSizeOver200() {
        Film film = Film.builder()
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.")
                .name("Film name")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .mpa(Mpa.builder()
                        .name("   ")
                        .build())
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
        assertEquals("Максимальная длина описания - 200 символов",
                violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenReleaseDateNotValid() {
        Film film = Film.builder()
                .description("Description")
                .name("Name")
                .releaseDate(LocalDate.of(1800, 7, 16))
                .duration(148)
                .mpa(Mpa.builder()
                        .name("   ")
                        .build())
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года",
                violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenDurationNegative() {
        Film film = Film.builder()
                .description("Description")
                .name("Name")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(-148)
                .mpa(Mpa.builder()
                        .name("   ")
                        .build())
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
        assertEquals("Продолжительность фильма должна быть положительным числом",
                violations.iterator().next().getMessage());
    }

}
