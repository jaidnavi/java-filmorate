package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {
    private Long id;
    private String name;
}
