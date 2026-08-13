package ru.yandex.practicum.filmorate.mapper;
import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.ReviewDTO;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewDTO toReviewDTO(Review review);

    Review toReview(ReviewDTO reviewDTO);

    Collection<ReviewDTO> toReviewDTOCollection(Collection<Review> reviews);
}
