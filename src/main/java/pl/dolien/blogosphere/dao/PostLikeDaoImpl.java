package pl.dolien.blogosphere.dao;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.dolien.blogosphere.entity.Post;
import pl.dolien.blogosphere.entity.PostLike;
import pl.dolien.blogosphere.entity.User;

import java.util.List;

@Repository
public class PostLikeDaoImpl implements PostLikeDao {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void addLike(Post post, User user) {
        PostLike like = new PostLike();
        like.setUser(user);
        like.setPost(post);
        post.addLike(like);
        user.addLike(like);

        entityManager.merge(like);
    }

    @Override
    @Transactional
    public void removeLike(Post post, User user) {
        List<PostLike> likes = post.getLikes();

        PostLike likeToRemove = null;
        for (PostLike like : likes) {
            if (like.getUser().equals(user)) {
                likeToRemove = like;
                break;
            }
        }

        if (likeToRemove != null) {
            user.getLikes().remove(likeToRemove);
            likes.remove(likeToRemove);
            entityManager.remove(likeToRemove);
        } else {
            throw new IllegalArgumentException("User has not liked this post.");
        }
    }
}
