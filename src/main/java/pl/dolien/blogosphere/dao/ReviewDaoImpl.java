package pl.dolien.blogosphere.dao;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.dolien.blogosphere.entity.Post;
import pl.dolien.blogosphere.entity.Review;
import pl.dolien.blogosphere.entity.User;

import java.util.List;

@Repository
public class ReviewDaoImpl implements ReviewDao {
    private final EntityManager entityManager;

    @Autowired
    public ReviewDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Review> findAllByPostId(int postId) {
        Post post = entityManager.find(Post.class, postId);

        if(post == null) {
            return null;
        }

        return post.getReviews();
    }

    @Override
    public Review findById(int id) {
        return entityManager.find(Review.class, id);
    }

    @Override
    public List<Review> getAllByUserId(Long userId) {
        User user = entityManager.find(User.class, userId);

        if(user == null) {
            return null;
        }

        return user.getReviews();
    }

    @Override
    @Transactional
    public void save(Review review) {
        entityManager.merge(review);
    }

    @Override
    @Transactional
    public void delete(Review review) {
        entityManager.remove(review);
    }
}
