package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.ReviewDTO;

import java.util.Collection;

public interface ReviewService {
    ReviewDTO create(ReviewDTO reviewDTO);

    ReviewDTO update(ReviewDTO reviewDTO);

    Collection<ReviewDTO> findAll(int count);

    Collection<ReviewDTO> findReviewsByFilmId(Long filmId, int count);

    ReviewDTO get(Long reviewId);

    void deleteReviewById(Long reviewId);

    void addLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);

    void deleteLike(Long reviewId, Long userId);

    void deleteDislike(Long reviewId, Long userId);

}
