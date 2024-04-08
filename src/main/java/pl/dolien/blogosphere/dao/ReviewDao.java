package pl.dolien.blogosphere.dao;

import pl.dolien.blogosphere.entity.Review;

import java.util.List;

public interface ReviewDao {

    List<Review> findAllByPostId(int postId);

    List<Review> getAllByUserId(Long userId);

    Review findById(int id);

    void save(Review review);

    void delete(Review review);
}
