package ru.yandex.practicum.filmorate.mapper;
import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.DirectorDTO;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface DirectorMapper {

    DirectorDTO toDirectorDTO(Director director);

    Director toDirector(DirectorDTO directorDTO);

    Collection<DirectorDTO> toDirectorDTOCollection(Collection<Director> directors);
}
