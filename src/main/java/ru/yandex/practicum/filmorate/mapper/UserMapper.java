package ru.yandex.practicum.filmorate.mapper;
import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toUserDTO(User user);

    User toUser(UserDTO userDTO);

    Collection<UserDTO> toUserDTOCollection(Collection<User> users);
}
