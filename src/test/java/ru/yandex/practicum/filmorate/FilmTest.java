package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        // Инициализируем валидатор перед каждым тестом
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
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        Film film = Film.builder()
                .name("   ") // Пустое название
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
        assertEquals("Название фильма не может быть пустым",
                violations.iterator().next().getMessage());
    }

}
