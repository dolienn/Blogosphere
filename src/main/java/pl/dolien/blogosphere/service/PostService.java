package pl.dolien.blogosphere.service;

import pl.dolien.blogosphere.entity.Post;
import pl.dolien.blogosphere.entity.Review;
import pl.dolien.blogosphere.validation.WebPost;

import java.util.List;

public interface PostService {

    List<Post> findAll();

    List<Post> findByUserName(String userName);

    List<Post> findPostsByTitle(String title);

    Post findById(int id);

    void createPost(String title, String description, String note, String privacy, List<Review> reviews);

    void save(WebPost webPost);

    void deleteById(int id);

}
