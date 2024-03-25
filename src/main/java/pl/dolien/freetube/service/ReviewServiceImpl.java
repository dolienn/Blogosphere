package pl.dolien.freetube.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dolien.freetube.dao.ReviewDao;
import pl.dolien.freetube.entity.Post;
import pl.dolien.freetube.entity.Review;
import pl.dolien.freetube.entity.User;
import pl.dolien.freetube.validation.WebReview;

import java.sql.Timestamp;
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
    public void save(WebReview webReview) {
        Review review = new Review();
        review.setId(0);
        review.setComment(webReview.getComment());
        review.setDate(new Timestamp(System.currentTimeMillis()));
        review.setUser(webReview.getUser());
        review.setPost(webReview.getPost());
        reviewDao.save(review);
    }

    @Override
    public void delete(Review review) {
        User user = review.getUser();
        user.remove(review);
        Post post = review.getPost();
        post.remove(review);
        reviewDao.delete(review);
    }

    @Override
    public void deleteById(int id) {
        Review review = reviewDao.findById(id);
        User user = review.getUser();
        user.remove(review);
        Post post = review.getPost();
        post.remove(review);
        reviewDao.delete(review);
    }
}
