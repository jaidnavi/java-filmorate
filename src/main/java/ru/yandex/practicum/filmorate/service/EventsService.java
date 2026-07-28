package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Events;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventsStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class EventsService {
    private final EventsStorage eventsStorage;
    private final UserService userService;

    @Autowired
    public EventsService(EventsStorage eventsStorage, UserService userService) {
        this.eventsStorage = eventsStorage;
        this.userService = userService;
    }

    public void addNewEvent(Long userId, String eventType, Long entityId, String operation) {
        if (userId != null) {
            Optional<User> user = userService.get(userId);
            if (user.isEmpty()) {
                throw new NoDataFoundException("Пользователь с id " + userId + " не найден");
            }
        } else {
            throw new ValidationException("Не указан идентификатор пользователя");
        }
        if (eventType != null) {
            try {
                EventType.valueOf(eventType);
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Неверный код события " + eventType + ". Допустимы LIKE,REVIEW,FRIEND.");
            }
        } else {
            throw new ValidationException("Не указан тип события");
        }
        if (entityId == null) {
            throw new ValidationException("Не указан идентификатор сущности события");
        }
        if (operation != null) {
            try {
                OperationType.valueOf(operation);
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Неверный код операции " + operation + ". Допустимы REMOVE, ADD, UPDATE.");
            }
        } else {
            throw new ValidationException("Не указан код операции для события");
        }
        eventsStorage.addNewEvent(userId,eventType,entityId,operation);
    }

    public Collection<Events> getFeedByUserId(Long userId) {
        Optional<User> user = userService.get(userId);
        if (user.isPresent()) {
            return eventsStorage.getFeedByUserId(userId);
        } else {
            throw new NoDataFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    public Collection<Events> getAllFeed() {
            return eventsStorage.getAllFeed();
    }
}
