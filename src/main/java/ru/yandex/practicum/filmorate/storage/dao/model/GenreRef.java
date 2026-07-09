package ru.yandex.practicum.filmorate.storage.dao.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenreRef {
    private Long genreId;
    private Long filmId;
}
