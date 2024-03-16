package pl.dolien.freetube.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dolien.freetube.dao.ReviewDao;
import pl.dolien.freetube.entity.Review;

import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewDao reviewDao;

    @Override
    public List<Review> getAllByPostId(int postId) {
        return reviewDao.findAllByPostId(postId);
    }

    @Override
    public List<Review> getAllByUserId(Long userId) {
        return reviewDao.getAllByUserId(userId);
    }

    @Override
    public Review getById(int id) {
        return reviewDao.findById(id);
    }

    @Override
    public void createReview(String comment) {
        Review review = new Review(comment);
        review.setId(0);
        reviewDao.save(review);
    }

    @Override
    public void save(Review review) {
        review.setId(0);
        reviewDao.save(review);
    }

    @Override
    public void delete(Review review) {
        reviewDao.delete(review);
    }

    @Override
    public void deleteById(int id) {
        Review review = reviewDao.findById(id);
        reviewDao.delete(review);
    }
}
