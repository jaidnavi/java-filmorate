package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Events;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventsService;
import ru.yandex.practicum.filmorate.storage.EventsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class EventsServiceImpl implements EventsService {
    private final EventsStorage eventsStorage;
    private final UserStorage userStorage;

    @Autowired
    public EventsServiceImpl(EventsStorage eventsStorage, UserStorage userStorage) {
        this.eventsStorage = eventsStorage;
        this.userStorage = userStorage;
    }

    @Override
    public void addNewEvent(Long userId, EventType eventType, Long entityId, OperationType operation) {
        if (userId != null) {
            Optional<User> user = userStorage.get(userId);
            if (user.isEmpty()) {
                throw new NoDataFoundException("Пользователь с id " + userId + " не найден");
            }
        } else {
            throw new ValidationException("Не указан идентификатор пользователя");
        }
        if (eventType == null) {
            throw new ValidationException("Не указан тип события");
        }
        if (entityId == null) {
            throw new ValidationException("Не указан идентификатор сущности события");
        }
        if (operation == null) {
            throw new ValidationException("Не указан код операции для события");
        }
        eventsStorage.addNewEvent(userId, eventType, entityId, operation);
    }

    @Override
    public Collection<Events> getFeedByUserId(Long userId) {
        Optional<User> user = userStorage.get(userId);
        if (user.isPresent()) {
            return eventsStorage.getFeedByUserId(userId);
        } else {
            throw new NoDataFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    public Collection<Events> getAllFeed() {
        return eventsStorage.getAllFeed();
    }
}
