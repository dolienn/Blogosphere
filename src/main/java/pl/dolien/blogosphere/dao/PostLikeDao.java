package pl.dolien.blogosphere.dao;

import pl.dolien.blogosphere.entity.Post;
import pl.dolien.blogosphere.entity.User;

public interface PostLikeDao {

    void addLike(Post post, User user);

    void removeLike(Post post, User user);
}
