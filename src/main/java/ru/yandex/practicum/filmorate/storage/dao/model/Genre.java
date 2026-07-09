package ru.yandex.practicum.filmorate.storage.dao.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Genre {
    private Long genreId;
    private String genre;
}
