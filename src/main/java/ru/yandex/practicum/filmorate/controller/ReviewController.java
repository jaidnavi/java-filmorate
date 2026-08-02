package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.ReviewDTO;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ReviewDTO create(@Valid @RequestBody ReviewDTO reviewDTO) {
        return reviewService.create(reviewDTO);
    }

    @PutMapping
    public ReviewDTO update(@Valid @RequestBody ReviewDTO reviewDTO) {
        return reviewService.update(reviewDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable("id") Long reviewId) {
        reviewService.deleteReviewById(reviewId);
    }

    @GetMapping("/{id}")
    public ReviewDTO get(@PathVariable("id") Long reviewId) {
        return reviewService.get(reviewId);
    }

    @GetMapping
    public Collection<ReviewDTO> findReviewsByFilmId(@RequestParam(required = false) Long filmId,
                                                  @RequestParam(defaultValue = "10") @Min(1) int count) {
        if (filmId == null) {
            return reviewService.findAll(count);
        }
        return reviewService.findReviewsByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long reviewId, @PathVariable("userId") Long userId) {
        reviewService.addLike(reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") Long reviewId, @PathVariable("userId") Long userId) {
        reviewService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long reviewId, @PathVariable("userId") Long userId) {
        reviewService.deleteLike(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") Long reviewId, @PathVariable("userId") Long userId) {
        reviewService.deleteDislike(reviewId, userId);
    }

}
