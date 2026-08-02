package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {
    private Long id;
    @NotBlank(message = "Название жанра не может быть пустым")
    private String name;
}
