package ru.yandex.practicum.filmorate.storage.dao.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFriend {
    private Long userFriendIf;
    private Long userId;
    private Long friendId;
    private Boolean confirm;
}
