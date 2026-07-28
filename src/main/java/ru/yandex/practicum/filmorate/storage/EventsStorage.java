package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Events;

import java.util.Collection;

/**
 * Класс реализует работу с моделью "Лента событий"
 */
public interface EventsStorage {

    /** Метод создания нового события в ленте
     * @param userId идентификатор пользователя у которого произошло событие
     * @param eventType тип события
     * @param entityId идентификатор сущности с которой связано событие
     * @param operation операция события
     */
    void addNewEvent(Long userId,String eventType, Long entityId, String operation);

    /** Метод возвращает ленту событий пользователя.
     * @param userId идентификатор пользователя у которого нужно вернуть ленту
     * @return список событий
     */
    Collection<Events> getFeedByUserId(Long userId);

    /** Метод возвращает всю ленту событий.
     * @return список событий
     */
    Collection<Events> getAllFeed();
}
