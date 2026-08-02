package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface MpaMapper {

    MpaMapper INSTANCE = Mappers.getMapper(MpaMapper.class);

    MpaDTO toMpaDTO(Mpa mpa);

    Collection<MpaDTO> toMpaDTOCollection(Collection<Mpa> mpas);
}