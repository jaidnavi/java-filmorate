package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;

import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.EventsService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventsService eventsService;

    @Autowired
    public ReviewServiceImpl(ReviewStorage reviewStorage, ReviewLikeStorage reviewLikeStorage,
                             FilmStorage filmStorage, UserStorage userStorage, EventsService eventsService) {
        this.reviewStorage = reviewStorage;
        this.reviewLikeStorage = reviewLikeStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.eventsService = eventsService;
    }

    @Override
    public Review create(Review review) {
        getUserById(review.getUserId());
        getFilmById(review.getFilmId());
        Review savedReview = reviewStorage.create(review);
        eventsService.addNewEvent(review.getUserId(), EventType.REVIEW, review.getReviewId(), OperationType.ADD);
        log.info("Добавлен новый отзыв с id = {}", savedReview.getReviewId());
        return savedReview;
    }

    @Override
    public Review update(Review newReview) {
        if (newReview.getReviewId() == null) {
            log.warn("При обновлении отзыва не передан id");
            throw new ValidationException("Поле id должно быть заполнено");
        }
        getUserById(newReview.getUserId());
        getFilmById(newReview.getFilmId());
        Review review = reviewStorage.update(newReview);
        eventsService.addNewEvent(review.getUserId(), EventType.REVIEW, review.getReviewId(), OperationType.UPDATE);
        log.info("Обновлен отзыв с id = {}", review.getReviewId());
        return review;
    }

    @Override
    public Collection<Review> findAll(int count) {
        return reviewStorage.findAll(count);
    }

    @Override
    public Collection<Review> findReviewsByFilmId(Long filmId, int count) {
        getFilmById(filmId);
        return reviewStorage.findReviewsByFilmId(filmId, count);
    }

    @Override
    public Review get(Long reviewId) {
        return reviewStorage.get(reviewId).orElseThrow(() -> {
            log.error("Не найден отзыв с id {}", reviewId);
            return new NoDataFoundException("Отзыв с id = " + reviewId + " не найден");
        });
    }

    @Override
    public Review deleteReviewById(Long reviewId) {
        Review review = get(reviewId);
        reviewStorage.deleteReviewById(reviewId);
        eventsService.addNewEvent(review.getUserId(), EventType.REVIEW, review.getReviewId(), OperationType.REMOVE);
        log.info("Удален отзыв с id = {}", reviewId);
        return review;
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        get(reviewId);
        getUserById(userId);
        reviewLikeStorage.addLike(reviewId, userId);
        log.info("Добавлен лайк к отзыву с id = {} от пользователя с id = {}", reviewId, userId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        get(reviewId);
        getUserById(userId);
        reviewLikeStorage.addDislike(reviewId, userId);
        log.info("Добавлен дизлайк к отзыву с id = {} от пользователя с id = {}", reviewId, userId);
    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {
        get(reviewId);
        getUserById(userId);
        reviewLikeStorage.deleteLike(reviewId, userId);
        log.info("Удален лайк к отзыву с id = {} от пользователя с id = {}", reviewId, userId);
    }

    @Override
    public void deleteDislike(Long reviewId, Long userId) {
        get(reviewId);
        getUserById(userId);
        reviewLikeStorage.deleteDislike(reviewId, userId);
        log.info("Удален дизлайк к отзыву с id = {} от пользователя с id = {}", reviewId, userId);
    }

    private void getUserById(Long userId) {
        userStorage.get(userId).orElseThrow(() -> {
            log.error("При работе с отзывами не найден пользователь с id {}", userId);
            return new NoDataFoundException("Пользователь с id = " + userId + " не найден");
        });
    }

    private void getFilmById(Long filmId) {
        filmStorage.get(filmId).orElseThrow(() -> {
            log.error("При работе с отзывами не найден фильм с id {}", filmId);
            return new NoDataFoundException("Фильм с id = " + filmId + " не найден");
        });
    }
}
