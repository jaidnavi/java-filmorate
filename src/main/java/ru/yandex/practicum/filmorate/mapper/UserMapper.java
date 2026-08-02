package ru.yandex.practicum.filmorate.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toUserDTO(User user);

    User toUser(UserDTO userDTO);

    Collection<UserDTO> toUserDTOCollection(Collection<User> users);

    // Используйте default метод для Optional
    default Optional<UserDTO> toOptionalUserDTO(Optional<User> optionalFilm) {
        return optionalFilm.map(this::toUserDTO);
    }
}
