package pl.dolien.blogosphere.dao;

import pl.dolien.blogosphere.entity.Review;
import pl.dolien.blogosphere.entity.User;

public interface ReviewLikeDao {

    void addLike(Review review, User user);

    void removeLike(Review review, User user);
}
