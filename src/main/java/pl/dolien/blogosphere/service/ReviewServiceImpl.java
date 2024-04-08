package pl.dolien.blogosphere.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dolien.blogosphere.entity.Post;
import pl.dolien.blogosphere.entity.Review;
import pl.dolien.blogosphere.entity.User;
import pl.dolien.blogosphere.validation.WebReview;
import pl.dolien.blogosphere.dao.ReviewDao;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewDao reviewDao;

    @Override
    public List<Review> findAllByPostId(int postId) {
        return reviewDao.findAllByPostId(postId);
    }

    @Override
    public List<Review> findAllByUserId(Long userId) {
        return reviewDao.getAllByUserId(userId);
    }

    @Override
    public Review findById(int id) {
        return reviewDao.findById(id);
    }

    @Override
    public void save(WebReview webReview) {
        Review review = reviewDao.findById(webReview.getId());

        if(review == null) {
            review = new Review();
            review.setDate(new Timestamp(System.currentTimeMillis()));
            review.setUser(webReview.getUser());
        } else {
            review.setEdited(true);
        }

        review.setId(webReview.getId());
        review.setEditDate(new Timestamp(System.currentTimeMillis()));
        review.setComment(webReview.getComment());
        review.setPost(webReview.getPost());
        reviewDao.save(review);
    }

    @Override
    public void delete(Review review) {
        User user = review.getUser();

        if(user != null) {
            user.remove(review);
        }

        Post post = review.getPost();
        post.remove(review);
        reviewDao.delete(review);
    }

    @Override
    public void deleteById(int id) {
        Review review = reviewDao.findById(id);
        User user = review.getUser();

        if(user != null) {
            user.remove(review);
        }

        Post post = review.getPost();
        post.remove(review);
        reviewDao.delete(review);
    }
}
