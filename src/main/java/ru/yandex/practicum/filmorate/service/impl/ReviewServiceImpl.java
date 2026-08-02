package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDTO;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;

import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
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
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventsService eventsService;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewDTO create(ReviewDTO reviewDTO) {
        log.info(reviewDTO.toString());
        Review review = reviewMapper.toReview(reviewDTO);
        log.info(review.toString());
        getUserById(review.getUserId());
        getFilmById(review.getFilmId());
        Review savedReview = reviewStorage.create(review);
        eventsService.addNewEvent(review.getUserId(), EventType.REVIEW, review.getReviewId(), OperationType.ADD);
        log.info("Добавлен новый отзыв с id = {}", savedReview.getReviewId());
        return reviewMapper.toReviewDTO(savedReview);
    }

    @Override
    public ReviewDTO update(ReviewDTO reviewDTO) {
        Review newReview = reviewMapper.toReview(reviewDTO);
        if (newReview.getReviewId() == null) {
            log.warn("При обновлении отзыва не передан id");
            throw new ValidationException("Поле id должно быть заполнено");
        }
        getUserById(newReview.getUserId());
        getFilmById(newReview.getFilmId());
        Review review = reviewStorage.update(newReview);
        eventsService.addNewEvent(review.getUserId(), EventType.REVIEW, review.getReviewId(), OperationType.UPDATE);
        log.info("Обновлен отзыв с id = {}", review.getReviewId());
        return reviewMapper.toReviewDTO(review);
    }

    @Override
    public Collection<ReviewDTO> findReviewsByFilmId(Long filmId, int count) {
        if (count < 1) {
            throw new ValidationException("Не корректное значение параметра count");
        }
        if (filmId == null) {
            return reviewMapper.toReviewDTOCollection(reviewStorage.findAll(count));
        } else {
            getFilmById(filmId);
            return reviewMapper.toReviewDTOCollection(reviewStorage.findReviewsByFilmId(filmId, count));
        }
    }

    @Override
    public ReviewDTO get(Long reviewId) {
        return reviewStorage.get(reviewId).map(reviewMapper::toReviewDTO).orElseThrow(() -> {
            log.error("Не найден отзыв с id {}", reviewId);
            return new NoDataFoundException("Отзыв с id = " + reviewId + " не найден");
        });
    }

    @Override
    public void deleteReviewById(Long reviewId) {
        Review review = reviewStorage.get(reviewId).orElseThrow(() -> {
            log.error("Не найден отзыв с id {}", reviewId);
            return new NoDataFoundException("Отзыв с id = " + reviewId + " не найден");
        });
        reviewStorage.deleteReviewById(reviewId);
        eventsService.addNewEvent(review.getUserId(), EventType.REVIEW, review.getReviewId(), OperationType.REMOVE);
        log.info("Удален отзыв с id = {}", reviewId);
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
