package ru.yandex.practicum.filmorate.mapper;
import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreDTO toGenreDTO(Genre genre);

    Collection<GenreDTO> toGenreDTOCollection(Collection<Genre> genres);

}
