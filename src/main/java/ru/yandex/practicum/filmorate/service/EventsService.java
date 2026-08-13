package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Events;
import ru.yandex.practicum.filmorate.model.OperationType;

import java.util.Collection;

public interface EventsService {

    void addNewEvent(Long userId, EventType eventType, Long entityId, OperationType operation);

    Collection<Events> getFeedByUserId(Long userId);

    Collection<Events> getAllFeed();

}
