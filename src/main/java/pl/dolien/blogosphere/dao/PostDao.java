package pl.dolien.blogosphere.dao;

import pl.dolien.blogosphere.entity.Post;

import java.util.List;

public interface PostDao {

    List<Post> getAllPost();

    List<Post> findPostByUsername(String userName);

    Post findPostById(int id);

    List<Post> findPostsByTitle(String title);

    void save(Post note);

    void delete(Post note);

}
