package pl.dolien.freetube.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dolien.freetube.dao.PostDao;
import pl.dolien.freetube.dao.ReviewDao;
import pl.dolien.freetube.dao.UserDao;
import pl.dolien.freetube.entity.Post;
import pl.dolien.freetube.entity.Review;
import pl.dolien.freetube.entity.User;
import pl.dolien.freetube.validation.WebPost;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    @Autowired
    private PostDao postDao;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Override
    public List<Post> findAll() {
        return postDao.getAllPost();
    }

    @Override
    public List<Post> findByUserName(String userName) {
        return postDao.findPostByUsername(userName);
    }

    @Override
    public List<Post> findPostsByTitle(String title) {
        return postDao.findPostsByTitle(title);
    }


    @Override
    public Post findById(int id) {
        return postDao.findPostById(id);
    }

    @Override
    public void createPost(String title, String description, String note, String privacy, List<Review> reviews) {
        Post post = new Post(title, description, note, privacy, reviews);
        post.setId(0);
        postDao.save(post);
    }   

    @Override
    public void save(WebPost webPost) {
        Post post = postDao.findPostById(webPost.getId());

        if(post == null) {
            post = new Post();
            post.setDate(new Timestamp(System.currentTimeMillis()));
        }

        post.setId(webPost.getId());
        post.setTitle(webPost.getTitle());
        post.setDescription(webPost.getDescription());
        post.setNote(webPost.getNote());
        post.setEdited(new Timestamp(System.currentTimeMillis()));
        post.setPrivacy(webPost.getPrivacy());
        post.setUser(webPost.getUser());

        postDao.save(post);
    }

    @Override
    public void deleteById(int id) {
        Post post = postDao.findPostById(id);

        User user = post.getUser();
        user.remove(post);

        List<Review> reviews = new ArrayList<>(post.getReviews());
        Iterator<Review> iterator = reviews.iterator();
        while (iterator.hasNext()) {
            Review review = iterator.next();
            reviewService.delete(review);
            iterator.remove();
        }

        postDao.delete(post);
    }

    @Override
    public void delete(Post post) {
        postDao.delete(post);
    }

}
