package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.UserDTO;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;


public interface UserService {

    UserDTO create(UserDTO userDTO);

    UserDTO update(UserDTO userDTO);

    Collection<UserDTO> findAll();

    Optional<UserDTO> get(Long userId);

    UserDTO addFriend(Long userId, Long friendUserId);

    void deleteFriend(Long userId, Long friendUserId);

    Collection<UserDTO> findFriends(Long userId);

    Set<UserDTO> findCommonFriends(Long userId, Long otherId);

    Collection<FilmDTO> findRecommendations(Long userId);

    void delete(Long userId);
}
