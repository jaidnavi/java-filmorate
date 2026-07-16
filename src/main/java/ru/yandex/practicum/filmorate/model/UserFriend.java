package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFriend {

    private Long userFriendId;
    @NotNull(message = "id пользователя должен быть указан")
    @Positive(message = "id пользователя должен быть положительным")
    private Long userId;
    @NotNull(message = "id друга должен быть указан")
    @Positive(message = "id друга должен быть положительным")
    private Long friendId;
}
