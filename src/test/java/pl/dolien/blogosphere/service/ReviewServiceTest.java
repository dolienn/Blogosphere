package pl.dolien.blogosphere.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pl.dolien.blogosphere.entity.Post;
import pl.dolien.blogosphere.entity.Review;
import pl.dolien.blogosphere.entity.User;
import pl.dolien.blogosphere.validation.WebPost;
import pl.dolien.blogosphere.validation.WebReview;
import pl.dolien.blogosphere.validation.WebUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest
public class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

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
                .build();

        postService.save(webPost1);

        Post post = null;
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        for (Post thePost : posts) {
            post = thePost;
        }

        WebReview webReview1 = WebReview.builder()
                .comment("The best post!")
                .post(post)
                .user(user)
                .build();

        WebReview webReview2 = WebReview.builder()
                .comment("The worst post!")
                .post(post)
                .user(user)
                .build();

        reviewService.save(webReview1);
        reviewService.save(webReview2);
    }

    @Test
    public void findAllReviewsByPostIdWithPostIdExists() {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        assertEquals(1, posts.size());

        Post post = posts.get(0);

        List<Review> reviews = reviewService.findAllByPostId(post.getId());
        assertEquals(2, reviews.size());
    }

    @Test
    public void findAllReviewsByPostIdWithPostIdNotFound() {
        List<Review> reviews = reviewService.findAllByPostId(9999);
        assertNull(reviews);
    }

    @Test
    public void findAllReviewsByUserIdWithUserIdExists() {
        User user = userService.findByUserName("test_user_1");
        List<Review> reviews = reviewService.findAllByUserId(user.getId());
        assertEquals(2, reviews.size());
    }

    @Test
    public void findAllReviewsByUserIdWithUserIdNotFound() {
        List<Review> reviews = reviewService.findAllByUserId(99999L);
        assertNull(reviews);
    }

    @Test
    public void findReviewByIdWithReviewIdExists() {
        User user = userService.findByUserName("test_user_1");
        List<Review> reviews = reviewService.findAllByUserId(user.getId());
        Review expectedReview = reviews.get(0);
        Review review = reviewService.findById(expectedReview.getId());
        assertEquals(expectedReview.getComment(), review.getComment());
    }

    @Test
    public void findReviewByIdWithReviewIdNotFound() {
        Review review = reviewService.findById(9999);
        assertNull(review);
    }

    @Test
    public void deleteReviewById() {
        WebUser webUser2 = WebUser.builder()
                .userName("test_user_2")
                .password("test123")
                .email("test2example.com")
                .lastName("Tester")
                .firstName("Larry")
                .build();

        userService.save(webUser2);

        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        User user = userService.findByUserName("test_user_2");
        assertNotNull(user);
        WebReview webReview = WebReview.builder()
                .comment("Not that bad")
                .post(post)
                .user(user)
                .build();
        reviewService.save(webReview);

        User theSameUser = userService.findByUserName("test_user_2");
        assertEquals(1, theSameUser.getReviews().size());

        Review review = theSameUser.getReviews().get(0);
        reviewService.deleteById(review.getId());

        User theSameUserAgain = userService.findByUserName("test_user_2");

        assertEquals(0, theSameUserAgain.getReviews().size());
    }

    @AfterEach
    public void delete() {

        List<Post> posts = postService.findAll();

        for (Post post : posts) {
            List<Review> reviews = post.getReviews();
            for (Review review : reviews) {
                reviewService.deleteById(review.getId());
            }
            postService.deleteById(post.getId());
        }

        List<User> users = userService.findAll();

        for (User user : users) {
            userService.deleteUser(user.getUserName());
        }
    }
}
