package pl.dolien.freetube.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;
import pl.dolien.freetube.dao.RoleDao;
import pl.dolien.freetube.dao.UserDao;
import pl.dolien.freetube.dao.PostDao;
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
public class UserServiceTest {

    @Autowired
    private JdbcTemplate jdbc;


//    @Value("${sql.script.create.user}")
//    private String sqlAddUser;

//    @Value("${sql.script.create.role.employee}")
//    private String sqlAddRoleEmployee;
//
//    @Value("${sql.script.create.role.manager}")
//    private String sqlAddRoleManager;
//
//    @Value("${sql.script.create.role.admin}")
//    private String sqlAddRoleAdmin;
//
//    @Value("${sql.script.create.user.relation.role}")
//    private String sqlAddRelation;

//    @Value("${sql.script.delete.user}")
//    private String sqlDeleteUser;

//    @Value("${sql.script.delete.role}")
//    private String sqlDeleteRole;
//
//    @Value("${sql.script.delete.relation}")
//    private String sqlDeleteRelation;

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

        System.out.println(user.getPosts());

        assertEquals("something@dolien.pl", user.getEmail(), "Lines should matches");
        assertEquals(Arrays.asList(userPost, userPost2), user.getPosts(), "Lists should matches");
        assertEquals(Arrays.asList(review, review1), user.getReviews(), "Lists should matches");
        assertEquals(Arrays.asList(review, review1), userPost.getReviews(), "Lists should matches");
        assertNull(userPost2.getReviews());
    }

    @Test
    public void findAllUsers() {
        assertNotNull(userService.findAll(), "Should be not null");
    }

    @Test
    public void isUserNullCheck() {
        assertTrue(userService.checkIfUserIsNull("some"), "Should be true");
        assertFalse(userService.checkIfUserIsNull("a"), "Should be false");
    }

    @Test
    public void checkIfLoadUserByUsernameIsNotNull() {
        assertNotNull(userService.loadUserByUsername("some"));
        assertThrows(UsernameNotFoundException.class,
                () -> { userService.loadUserByUsername("a"); }, "Should throw exception");
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
