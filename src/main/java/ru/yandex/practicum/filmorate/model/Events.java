package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Класс описывает сущность "Лента событий"
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Events {

    /**
     * Идентификатор события
     */
    private Long eventId;

    /**
     * Идентификатор пользователя
     */
    @NotBlank(message = "Идентификатор пользователя не может быть пустым")
    private Long userId;

    /**
     * Код типа события
     * одно из значений LIKE, REVIEW или FRIEND
     */
    @NotBlank(message = "Тип события не может быть пустым")
    @Size(max = 100, message = "Максимальная длина типа - 100 символов")
    private EventType eventType;

    /**
     * Код выполненной операции
     * одно из значениий REMOVE, ADD, UPDATE
     */
    @NotBlank(message = "Код операции не может быть пустым")
    @Size(max = 100, message = "Максимальная длина операции - 100 символов")
    private OperationType operation;

    /**
     * Временная метка даты (в милисекундах)
     */
    private Long timestamp;

    /**
     * Идентификатор сущности, с которой произошло событие
     */
   private Long entityId;
}
