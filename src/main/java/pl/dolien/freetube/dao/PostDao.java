package pl.dolien.freetube.dao;

import pl.dolien.freetube.entity.Post;

public interface PostDao {

    Iterable<Post> getAllPost();

    Iterable<Post> findPostByUsername(String userName);

    Post findPostById(int id);

    Post findPostByTitle(String title);

    void save(Post note);

    void delete(Post note);

}
