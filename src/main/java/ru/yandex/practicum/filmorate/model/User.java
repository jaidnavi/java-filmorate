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
public class User {
    private static final String SPACE_SIMBOL = " ";

    private Long userId;
    private Set<Long> friends;
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна содержать символ @")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    @AssertTrue(message = "Логин не должен содержать пробелы")
    private boolean isLoginWithoutSpaces() {
        if (login == null) {
            return true;
        }
        return !login.contains(SPACE_SIMBOL);
    }

}
