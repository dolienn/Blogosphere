package pl.dolien.freetube.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dolien.freetube.dao.ReviewDao;
import pl.dolien.freetube.entity.Review;

import java.util.List;

public interface ReviewService {

    List<Review> getAllByPostId(int postId);

    List<Review> getAllByUserId(Long userId);

    Review getById(int id);

    void createReview(String comment);

    void save(Review review);

    void delete(Review review);

    void deleteById(int id);
}
