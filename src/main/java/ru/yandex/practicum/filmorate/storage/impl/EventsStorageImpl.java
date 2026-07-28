package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Events;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.storage.EventsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Реализация интерфейса для работы с моделью "Лента событий"
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class EventsStorageImpl implements EventsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addNewEvent(Long userId, String eventType, Long entityId, String operation) {
        String insert = """
                INSERT INTO events (user_id, event_type, entity_id, operation, timestamp)
                VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
                """;
            int rowsInserted = jdbcTemplate.update(insert, userId, eventType, entityId, operation);
            if (rowsInserted == 0) {
                throw new InternalServerException("Не удалось вставить данные в таблицу events");
            } else {
                log.info("Событие для пользователя userId={} успешно добавлено в ленту", userId);
            }

    }

    @Override
    public Collection<Events> getFeedByUserId(Long userId) {
        if (userId == null) {
            throw new ValidationException("Не указан идентификатор пользователя");
        }
        String sql = """
                    SELECT event_id, user_id, event_type, entity_id, operation, timestamp
                      FROM events
                     WHERE user_id = ?
                    ORDER BY timestamp desc
                    """;
        try {
            return jdbcTemplate.query(sql, this::mapRowToEvents, userId);
        } catch (EmptyResultDataAccessException ignored) {
            throw new NoDataFoundException("Не найдены события для пользователя с user_id " + userId);
        }
    }

    private Events mapRowToEvents(ResultSet rs, int rowNum) throws SQLException {
        Events events = new Events();
        events.setEventId(rs.getLong("event_id"));
        events.setUserId(rs.getLong("user_id"));
        events.setEventType(EventType.valueOf(rs.getString("event_type")));
        events.setEntityId(rs.getLong("entity_id"));
        events.setOperation(OperationType.valueOf(rs.getString("operation")));
        events.setTimestamp(rs.getTimestamp("timestamp"));
        return events;
    }

    @Override
    public Collection<Events> getAllFeed() {
        String sql = """
                    SELECT event_id, user_id, event_type, entity_id, operation, timestamp
                      FROM events
                    ORDER BY timestamp desc
                    """;
        try {
            return jdbcTemplate.query(sql, this::mapRowToEvents);
        } catch (EmptyResultDataAccessException ignored) {
            throw new NoDataFoundException("Не найдены события в базе данных ");
        }
    }
}
