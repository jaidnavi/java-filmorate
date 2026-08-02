package ru.yandex.practicum.filmorate.mapper;
import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;


@Mapper(componentModel = "spring")
public interface FilmMapper {

    FilmDTO toFilmDTO(Film film);

    Film toFilm(FilmDTO filmDTO);

    Collection<FilmDTO> toFilmDTOCollection(Collection<Film> filmDTOs);

    // Используйте default метод для Optional
    default Optional<FilmDTO> toOptionalFilmDTO(Optional<Film> optionalFilm) {
        return optionalFilm.map(this::toFilmDTO);
    }
}
