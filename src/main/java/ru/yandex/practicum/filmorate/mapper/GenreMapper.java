package ru.yandex.practicum.filmorate.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreMapper INSTANCE = Mappers.getMapper(GenreMapper.class);
    GenreDTO toGenreDTO(Genre genre);
    Collection<GenreDTO> toGenreDTOCollection(Collection<Genre> genres);
}
