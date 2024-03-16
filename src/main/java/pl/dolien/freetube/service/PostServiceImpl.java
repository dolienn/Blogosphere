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
    public Iterable<Post> findAll() {
        return postDao.getAllPost();
    }

    @Override
    public Iterable<Post> findByUserName(String userName) {
        return postDao.findPostByUsername(userName);
    }
    

    @Override
    public Post findById(int id) {
        return postDao.findPostById(id);
    }

    @Override
    public void createPost(String title, String note, String privacy, List<Review> reviews) {
        Post post = new Post(title, note, privacy, reviews);
        post.setId(0);
        postDao.save(post);
    }   

    @Override
    public void save(Post post) {
        post.setId(0);
        postDao.save(post);
    }

    @Override
    public void deleteByTitle(String title) {
        Post post = postDao.findPostByTitle(title);
        postDao.delete(post);
    }

    @Override
    public void deleteById(int id) {
        Post post = postDao.findPostById(id);

        User user = post.getUser();
        user.remove(post);

        List<Review> reviews = post.getReviews();
        for (Review review : reviews) {
            reviewService.delete(review);
        }
        postDao.delete(post);
    }

    @Override
    public void delete(Post post) {
        postDao.delete(post);
    }

}
