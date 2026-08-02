package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Events;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventsService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserFriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserFriendStorage userFriendStorage;
    private final FilmStorage filmStorage;
    private final EventsService eventsService;
    private final UserMapper userMapper;
    private final FilmMapper filmMapper;

    @Autowired
    public UserServiceImpl(UserStorage userStorage, UserFriendStorage userFriendStorage, FilmStorage filmStorage, EventsService eventsService, UserMapper userMapper, FilmMapper filmMapper) {
        this.userStorage = userStorage;
        this.userFriendStorage = userFriendStorage;
        this.filmStorage = filmStorage;
        this.eventsService = eventsService;
        this.userMapper = userMapper;
        this.filmMapper = filmMapper;
    }

    @Override
    public UserDTO create(UserDTO userDTO) {
        User user = userMapper.toUser(userDTO);
        return userMapper.toUserDTO(userStorage.create(user));
    }

    @Override
    public UserDTO update(UserDTO userDTO) {
        User user = userMapper.toUser(userDTO);
        return userMapper.toUserDTO(userStorage.update(user));
    }

    @Override
    public Collection<UserDTO> findAll() {
        return userMapper.toUserDTOCollection(userStorage.findAll());
    }

    @Override
    public Optional<UserDTO> get(Long userId) {
        return userMapper.toOptionalUserDTO(userStorage.get(userId));
    }

    @Override
    public UserDTO addFriend(Long userId, Long friendUserId) {
        userStorage.get(userId).orElseThrow(() -> {
            log.error("При добавлении друзей, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При добавлении друзей, не найден пользователь с id " + userId);
        });

        userStorage.get(friendUserId).orElseThrow(() -> {
            log.error("При добавлении друзей, не найден друг с id {}", friendUserId);
            return new NoDataFoundException("При добавлении друзей, не найден друг с id " + friendUserId);
        });

        Set<Long> currentFriends = userFriendStorage.findFriendIdByUserId(userId);
        Set<Long> friendsSet = new HashSet<>(currentFriends != null ? currentFriends : Collections.emptySet());

        if (friendsSet.contains(friendUserId)) {
            log.info("Друг с id {} уже является другом пользователю с ид {}.", friendUserId, userId);
        } else {
            friendsSet.add(friendUserId);
            userFriendStorage.saveFriend(userId, friendsSet);
            eventsService.addNewEvent(userId, EventType.FRIEND, friendUserId, OperationType.ADD);
            log.info("Пользователю {} успешно добавлен новый друг {}", userId, friendUserId);
        }

        User user = userStorage.get(userId).orElseThrow(() -> {
            log.error("Ошибка при получении обновленного пользователя с id {}", userId);
            return new NoDataFoundException("Пользователь с id " + userId + " не найден после обновления");
        });

        return userMapper.toUserDTO(user);
    }

    @Override
    public void deleteFriend(Long userId, Long friendUserId) {

        userStorage.get(userId).orElseThrow(() -> {
            log.error("При удалении друзей, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При удалении друзей, не найден пользователь с id " + userId);
        });

        userStorage.get(friendUserId).orElseThrow(() -> {
            log.error("При удалении друзей, не найден друг с id {}", friendUserId);
            return new NoDataFoundException("При удалении друзей, не найден друг с id " + friendUserId);
        });

        userFriendStorage.deleteByUserId(userId, friendUserId);
        eventsService.addNewEvent(userId, EventType.FRIEND, friendUserId, OperationType.REMOVE);
        log.info("Пользователю {} успешно удален друг {}", userId, friendUserId);

        userStorage.get(userId).orElseThrow(() ->
                new NoDataFoundException("Пользователь не найден после удаления друга"));
    }

    @Override
    public Collection<UserDTO> findFriends(Long userId) {

        User user = userStorage.get(userId).orElseThrow(() -> {
            log.error("При поиске друзей, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При поиске друзей, не найден пользователь с id " + userId);
        });

        if (user.getFriends() == null) {
            return Collections.emptyList();
        }

        return user.getFriends().stream()
                .map(userStorage::get)
                .flatMap(Optional::stream)
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Set<UserDTO> findCommonFriends(Long userId, Long otherId) {
        Set<Long> userFriends = userStorage.get(userId)
                .map(User::getFriends)
                .orElseThrow(() -> new NoDataFoundException("Пользователь с id " + userId + " не найден"));

        Set<Long> otherFriends = userStorage.get(otherId)
                .map(User::getFriends)
                .orElseThrow(() -> new NoDataFoundException("Пользователь с id " + otherId + " не найден"));

        if (userFriends == null || otherFriends == null) {
            return Collections.emptySet();
        }

        Set<Long> commonIds = userFriends.stream()
                .filter(otherFriends::contains)
                .collect(Collectors.toSet());

        if (commonIds.isEmpty()) {
            return Collections.emptySet();
        }

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(userStorage::get)
                .flatMap(Optional::stream)
                .map(userMapper::toUserDTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<FilmDTO> findRecommendations(Long userId) {
        userStorage.get(userId).orElseThrow(() -> {
            log.error("При поиске рекомендаций, не найден пользователь с id {}", userId);
            return new NoDataFoundException("При поиске рекомендаций, не найден пользователь с id " + userId);
        });

        return filmStorage.findRecommendations(userId).stream().map(filmMapper::toFilmDTO).collect(Collectors.toSet());
    }

    @Override
    public void delete(Long userId) {
        userStorage.delete(userId);
    }

    @Override
    public Collection<Events> getAllFeed() {
        return eventsService.getAllFeed();
    }

    @Override
    public Collection<Events> getFeedByUserId(Long userId) {
        return eventsService.getFeedByUserId(userId);
    }
}
