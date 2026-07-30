package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, ReviewLikeStorage reviewLikeStorage,
                         FilmStorage filmStorage, UserStorage userStorage) {
        this.reviewStorage = reviewStorage;
        this.reviewLikeStorage = reviewLikeStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Review create(Review review) {
        getUserById(review.getUserId());
        getFilmById(review.getFilmId());
        Review savedReview = reviewStorage.create(review);
        log.info("Добавлен новый отзыв с id = {}", savedReview.getReviewId());
        return savedReview;
    }

    public Review update(Review newReview) {
        if (newReview.getReviewId() == null) {
            log.warn("При обновлении отзыва не передан id");
            throw new ValidationException("Поле id должно быть заполнено");
        }
        getUserById(newReview.getUserId());
        getFilmById(newReview.getFilmId());

        Review review = reviewStorage.update(newReview);
        log.info("Обновлен отзыв с id = {}", review.getReviewId());
        return review;
    }

    public Collection<Review> findAll(int count) {
        return reviewStorage.findAll(count);
    }

    public Collection<Review> findReviewsByFilmId(Long filmId, int count) {
        getFilmById(filmId);
        return reviewStorage.findReviewsByFilmId(filmId, count);
    }

    public Review get(Long reviewId) {
        return reviewStorage.get(reviewId).orElseThrow(() -> {
            log.error("Не найден отзыв с id {}", reviewId);
            return new NoDataFoundException("Отзыв с id = " + reviewId + " не найден");
        });
    }

    public void deleteReviewById(Long reviewId) {
        get(reviewId);
        reviewStorage.deleteReviewById(reviewId);
        log.info("Удален отзыв с id = {}", reviewId);
    }

    public void addLike(Long reviewId, Long userId) {
        get(reviewId);
        getUserById(userId);
        reviewLikeStorage.addLike(reviewId, userId);
        log.info("Добавлен лайк к отзыву с id = {} от пользователя с id = {}", reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        get(reviewId);
        getUserById(userId);
        reviewLikeStorage.addDislike(reviewId, userId);
        log.info("Добавлен дизлайк к отзыву с id = {} от пользователя с id = {}", reviewId, userId);
    }

    public void deleteLike(Long reviewId, Long userId) {
        get(reviewId);
        getUserById(userId);
        reviewLikeStorage.deleteLike(reviewId, userId);
        log.info("Удален лайк к отзыву с id = {} от пользователя с id = {}", reviewId, userId);
    }

    public void deleteDislike(Long reviewId, Long userId) {
        get(reviewId);
        getUserById(userId);
        reviewLikeStorage.deleteDislike(reviewId, userId);
        log.info("Удален дизлайк к отзыву с id = {} от пользователя с id = {}", reviewId, userId);
    }

    private User getUserById(Long userId) {
        return userStorage.get(userId).orElseThrow(() -> {
            log.error("При работе с отзывами не найден пользователь с id {}", userId);
            return new NoDataFoundException("Пользователь с id = " + userId + " не найден");
        });
    }

    private Film getFilmById(Long filmId) {
        return filmStorage.get(filmId).orElseThrow(() -> {
            log.error("При работе с отзывами не найден фильм с id {}", filmId);
            return new NoDataFoundException("Фильм с id = " + filmId + " не найден");
        });
    }
}
