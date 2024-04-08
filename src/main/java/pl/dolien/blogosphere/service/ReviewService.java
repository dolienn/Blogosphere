package pl.dolien.blogosphere.service;

import pl.dolien.blogosphere.entity.Review;
import pl.dolien.blogosphere.validation.WebReview;

import java.util.List;

public interface ReviewService {

    List<Review> findAllByPostId(int postId);

    List<Review> findAllByUserId(Long userId);

    Review findById(int id);

    void save(WebReview webReview);

    void delete(Review review);

    void deleteById(int id);
}
