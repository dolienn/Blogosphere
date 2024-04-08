package pl.dolien.blogosphere.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pl.dolien.blogosphere.entity.Post;
import pl.dolien.blogosphere.entity.User;
import pl.dolien.blogosphere.validation.WebPost;
import pl.dolien.blogosphere.validation.WebUser;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        WebUser webUser1 = WebUser.builder()
                .userName("test_user_1")
                .password("test123")
                .email("test1@example.com")
                .lastName("Tester")
                .firstName("John")
                .build();

        userService.save(webUser1);

        User user = userService.findByUserName("test_user_1");

        WebPost webPost1 = WebPost.builder()
                .title("Harry Potter")
                .description("The best book")
                .note("Harry Potter book is very popular among people and i love it")
                .privacy("public")
                .user(user)
                .build();

        WebPost webPost2 = WebPost.builder()
                .title("The Lord of the Rings")
                .description("An epic fantasy novel")
                .note("The Lord of the Rings is a classic fantasy novel written by J.R.R. Tolkien.")
                .privacy("private")
                .user(user)
                .build();

        postService.save(webPost1);
        postService.save(webPost2);
    }

    @Test
    public void findAllPosts() {
        List<Post> posts = postService.findAll();
        assertEquals(2, posts.size());
        assertNotEquals(0, posts.size());
    }

    @Test
    public void findPostsByUsernameWhenUserExists() {
        List<Post> posts = postService.findByUserName("test_user_1");
        assertEquals(2, posts.size());
        assertNotEquals(0, posts.size());
    }

    @Test
    public void findPostsByUsernameWhenUserNotFound() {
        List<Post> posts = postService.findByUserName("nonexistentUser");
        assertEquals(Collections.emptyList(), posts);
    }

    @Test
    public void findPostsByTitleWhenTitleExists() {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        assertEquals(1, posts.size());
        assertNotEquals(0, posts.size());
    }

    @Test
    public void findPostsByTitleWhenTitleNotFound() {
        List<Post> posts = postService.findPostsByTitle("Magic Pen");
        assertEquals(Collections.emptyList(), posts);
    }

    @Test
    public void findPostByIdWhenIdExists() {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post harryPotterPost = posts.get(0);
        Post post = postService.findById(harryPotterPost.getId());
        assertEquals("Harry Potter", post.getTitle());
    }

    @Test
    public void findPostByIdWhenIdNotFound() {
        Post post = postService.findById(32);
        assertNull(post);
    }

    @Test
    public void createPostWithoutValidationAndDeleteHimById() {
        postService.createPost("To Kill a Mockingbird", "A classic American novel",
                "To Kill a Mockingbird is a novel by Harper Lee published in 1960. It was immediately successful, winning the Pulitzer Prize, and has become a classic of modern American literature.",
                "public", null);

        List<Post> posts = postService.findPostsByTitle("To Kill a Mockingbird");
        assertEquals(1, posts.size());

        for (Post post : posts) {
            postService.deleteById(post.getId());
        }

        List<Post> samePosts = postService.findPostsByTitle("To Kill a Mockingbird");
        assertEquals(Collections.emptyList(), samePosts);
    }

    @AfterEach
    public void delete() {
        List<Post> posts = postService.findAll();

        for (Post post : posts) {
            postService.deleteById(post.getId());
        }

        List<User> users = userService.findAll();

        for (User user : users) {
            userService.deleteUser(user.getUserName());
        }
    }
}
