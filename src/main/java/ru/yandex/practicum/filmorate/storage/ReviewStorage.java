package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    Collection<Review> findAll(int count);

    Collection<Review> findReviewsByFilmId(Long filmId, int count);

    Optional<Review> get(Long reviewId);

    void deleteReviewById(Long reviewId);

}
