package pl.dolien.freetube.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pl.dolien.freetube.dao.PostDao;
import pl.dolien.freetube.dao.RoleDao;
import pl.dolien.freetube.dao.UserDao;
import pl.dolien.freetube.entity.Post;
import pl.dolien.freetube.entity.Review;
import pl.dolien.freetube.entity.Role;
import pl.dolien.freetube.entity.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application.properties")
@SpringBootTest
public class ReviewServiceTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private PostService postService;

    @Autowired
    private ReviewService reviewService;

    @BeforeEach
    public void setup() {
        Role userRoles = roleDao.findRoleByName("ROLE_EMPLOYEE");
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(userRoles);

        Post userPost = new Post("Harry Potter", "Welcome to my post, only I just wanna to so I love you guys!", "public");
        Post userPost2 = new Post("Magic Pen", "Welcome to my post, only I just wanna to so I hate you guys!", "private");

        Review review = new Review("Cool post!");
        Review review1 = new Review("Very helpful!");

        assertNull(userService.findByUserName("some"));
        userService.createUser("some", "$2a$10$gK6dKQQn6d1iPDk3rSywJ.qSHLcQdCgZeyPjrKapjNRAPLJzfj6Ha", "Some", "Thing", "something@dolien.pl", roles, null, null);
        User user = userService.findByUserName("some");
        user.add(userPost);
        user.add(userPost2);

        userPost.add(review);
        userPost.add(review1);
        user.add(review);
        user.add(review1);

        postService.save(userPost);

        assertEquals("something@dolien.pl", user.getEmail(), "Lines should matches");
        assertEquals(Arrays.asList(userPost, userPost2), user.getPosts(), "Lists should matches");
        assertEquals(Arrays.asList(review, review1), user.getReviews(), "Lists should matches");
        assertEquals(Arrays.asList(review, review1), userPost.getReviews(), "Lists should matches");
        assertNull(userPost2.getReviews());
    }

    @AfterEach
    public void deleteUser() {
        User user = userService.findByUserName("some");
        assertNotNull(user);

        List<Post> userPosts = user.getPosts();
        userService.deleteUser("some");

        for (Post post : userPosts) {
            postService.deleteById(post.getId());
            assertNull(postService.findById(post.getId()));
        }

        assertNull(userService.findByUserName("some"));
    }
}
