package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public enum SortType {
    YEAR("year"),
    LIKES("likes");

    private final String description;

    SortType(String description) {
        this.description = description;
    }
}
