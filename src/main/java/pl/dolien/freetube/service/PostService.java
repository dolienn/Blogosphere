package pl.dolien.freetube.service;

import org.springframework.web.multipart.MultipartFile;
import pl.dolien.freetube.entity.Post;
import pl.dolien.freetube.entity.Review;

import java.util.List;

public interface PostService {

    Iterable<Post> findAll();

    Iterable<Post> findByUserName(String userName);

    Post findById(int id);

    void createPost(String title, String note, String privacy, List<Review> reviews);

    void save(Post post);

    void deleteByTitle(String title);

    void deleteById(int id);

    void delete(Post post);
}
