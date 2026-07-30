package ru.yandex.practicum.filmorate.model;

public enum SearchByType {
    TITLE("title"),
    DIRECTOR("director");

    private final String description;

    SearchByType(String description) {
        this.description = description;
    }
}
