package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private Long id;
    @NotNull
    @Email
    private String email;

    @NotBlank
    @NotNull
    private String login;
    private String name;
    private LocalDate birthday;
}
