package pl.dolien.freetube.service;

import org.springframework.web.multipart.MultipartFile;
import pl.dolien.freetube.entity.Post;
import pl.dolien.freetube.entity.Review;
import pl.dolien.freetube.validation.WebPost;

import java.util.List;

public interface PostService {

    List<Post> findAll();

    List<Post> findByUserName(String userName);

    List<Post> findPostsByTitle(String title);

    Post findById(int id);

    void createPost(String title, String description, String note, String privacy, List<Review> reviews);

    void save(WebPost webPost);

    void deleteById(int id);

    void delete(Post post);
}
