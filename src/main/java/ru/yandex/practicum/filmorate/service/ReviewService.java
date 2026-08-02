package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewService {
    Review create(Review review);

    Review update(Review newReview);

    Collection<Review> findAll(int count);

    Collection<Review> findReviewsByFilmId(Long filmId, int count);

    Review get(Long reviewId);

    Review deleteReviewById(Long reviewId);

    void addLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);

    void deleteLike(Long reviewId, Long userId);

    void deleteDislike(Long reviewId, Long userId);

}
