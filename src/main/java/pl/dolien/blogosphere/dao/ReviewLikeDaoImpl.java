package pl.dolien.blogosphere.dao;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.dolien.blogosphere.entity.Review;
import pl.dolien.blogosphere.entity.ReviewLike;
import pl.dolien.blogosphere.entity.User;

import java.util.List;

@Repository
public class ReviewLikeDaoImpl implements ReviewLikeDao {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void addLike(Review review, User user) {
        ReviewLike like = new ReviewLike();
        like.setUser(user);
        like.setReview(review);
        review.addLike(like);

        entityManager.merge(like);
    }

    @Override
    @Transactional
    public void removeLike(Review review, User user) {
        List<ReviewLike> likes = review.getLikes();

        ReviewLike likeToRemove = null;
        for (ReviewLike like : likes) {
            if (like.getUser().equals(user)) {
                likeToRemove = like;
                break;
            }
        }

        if (likeToRemove != null) {
            likes.remove(likeToRemove);
            entityManager.remove(likeToRemove);
        } else {
            throw new IllegalArgumentException("User has not liked this review.");
        }
    }
}
