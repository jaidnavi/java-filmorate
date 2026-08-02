package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.UserDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDTOTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldValidateCorrectUser() {
        UserDTO user = UserDTO.builder()
                .email("test@yandex.ru")
                .login("valid_login")
                .name("kosticin")
                .birthday(LocalDate.of(1984, 6, 6))
                .build();

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {
        UserDTO user = UserDTO.builder()
                .email("           ")
                .login("valid_login")
                .name("kosticin")
                .birthday(LocalDate.of(1984, 6, 6))
                .build();


        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);

        assertEquals(2, violations.size());

        List<String> messages = violations.stream().map(ConstraintViolation::getMessage).toList();

        assertTrue(messages.containsAll(List.of(
                "Электронная почта не может быть пустой",
                "Электронная почта должна содержать символ @"
        )));

    }

    @Test
    void shouldFailWhenEmailNotEmail() {
        UserDTO user = UserDTO.builder()
                .email("mail.ru")
                .login("valid_login")
                .name("kosticin")
                .birthday(LocalDate.of(1984, 6, 6))
                .build();


        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);

        assertEquals(1, violations.size());

        assertEquals("Электронная почта должна содержать символ @",
                violations.iterator().next().getMessage());

    }

    @Test
    void shouldFailWhenLoginIsEmpty() {
        UserDTO user = UserDTO.builder()
                .email("test@yandex.ru")
                .login("")
                .name("kosticin")
                .birthday(LocalDate.of(1984, 6, 6))
                .build();


        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);

        assertEquals(1, violations.size());

        assertEquals("Логин не может быть пустым",
                violations.iterator().next().getMessage());

    }

    @Test
    void shouldFailWhenLoginContainsSpace() {
        UserDTO user = UserDTO.builder()
                .email("test@yandex.ru")
                .login("dolore ullamco")
                .name("kosticin")
                .birthday(LocalDate.of(1984, 6, 6))
                .build();


        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);

        assertEquals(1, violations.size());

        assertEquals("Логин не должен содержать пробелы",
                violations.iterator().next().getMessage());

    }

    @Test
    void shouldFailWhenBirthdayInFuture() {
        UserDTO user = UserDTO.builder()
                .email("test@yandex.ru")
                .login("dolore_ullamco")
                .name("kosticin")
                .birthday(LocalDate.of(2027, 6, 6))
                .build();


        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);

        assertEquals(1, violations.size());

        assertEquals("Дата рождения не может быть в будущем",
                violations.iterator().next().getMessage());

    }

}
