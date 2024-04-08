package pl.dolien.blogosphere.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;
import pl.dolien.blogosphere.dao.ReviewLikeDao;
import pl.dolien.blogosphere.dao.RoleDao;
import pl.dolien.blogosphere.entity.Post;
import pl.dolien.blogosphere.entity.Review;
import pl.dolien.blogosphere.entity.Role;
import pl.dolien.blogosphere.entity.User;
import pl.dolien.blogosphere.service.PostService;
import pl.dolien.blogosphere.service.ReviewService;
import pl.dolien.blogosphere.service.UserService;
import pl.dolien.blogosphere.validation.WebPost;
import pl.dolien.blogosphere.validation.WebReview;
import pl.dolien.blogosphere.validation.WebUser;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewLikeDao reviewLikeDao;

    @Autowired
    private RoleDao roleDao;

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

        WebUser webUser2 = WebUser.builder()
                .userName("test_user_2")
                .password("test123")
                .email("test2@example.com")
                .lastName("Tester")
                .firstName("Susan")
                .build();

        Role role = new Role("ROLE_USER");
        Role role2 = new Role("ROLE_ADMIN");
        roleDao.save(role);
        roleDao.save(role2);

        userService.save(webUser2);

        WebUser webUser3 = WebUser.builder()
                .userName("test_admin")
                .password("test123")
                .email("test3@example.com")
                .lastName("Tester")
                .firstName("Harry")
                .roles(Arrays.asList(roleDao.findRoleByName("ROLE_ADMIN"),
                        roleDao.findRoleByName("ROLE_USER")))
                .build();

        userService.save(webUser3);

        User user = userService.findByUserName("test_user_1");
        User user2 = userService.findByUserName("test_user_2");

        WebPost webPost1 = WebPost.builder()
                .title("Harry Potter")
                .description("The best book")
                .note("Harry Potter book is very popular among people and i love it")
                .privacy("public")
                .build();

        postService.save(webPost1);

        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);

        WebPost webPost2 = WebPost.builder()
                .title("The Lord")
                .description("An epic")
                .note("The Lord of the Rings is a classic fantasy novel written by J.R.R. Tolkien.")
                .privacy("private")
                .user(user2)
                .build();

        postService.save(webPost2);

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
    @WithMockUser(username = "test_user_1")
    public void editReviewHttpRequest() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        int postId = posts.get(0).getId();
        User user = userService.findByUserName("test_user_1");
        List<Review> reviews = reviewService.findAllByUserId(user.getId());
        Review review = reviews.get(0);
        MvcResult mvcResult = this.mockMvc.perform(post("/post/note/review/edit?postId=" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("reviewId", String.valueOf(review.getId()))
                        .param("id", "1")
                        .param("comment", "The best post!")
                        .param("postId", String.valueOf(postId))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()).andReturn();

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/post/note?postId=" + postId, redirectedUrl);
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void editReviewHttpRequestWithPostDoesNotExists() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/post/note/review/edit?postId=" + 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("reviewId", "1")
                        .param("id", "1")
                        .param("comment", "The best post!")
                        .param("postId", String.valueOf(999))
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @WithMockUser(username = "nonexistentUser")
    public void editReviewHttpRequestWithUserDoesNotExists() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        int postId = posts.get(0).getId();
        MvcResult mvcResult = this.mockMvc.perform(post("/post/note/review/edit?postId=" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("reviewId", "1")
                        .param("id", "1")
                        .param("comment", "The best post!")
                        .param("postId", String.valueOf(postId))
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void editReviewHttpRequestWithInvalidValidation() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        int postId = posts.get(0).getId();
        User user = userService.findByUserName("test_user_1");
        List<Review> reviews = reviewService.findAllByUserId(user.getId());
        Review review = reviews.get(0);
        MvcResult mvcResult = this.mockMvc.perform(post("/post/note/review/edit?postId=" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("reviewId", String.valueOf(review.getId()))
                        .param("id", "1")
                        .param("comment", "")
                        .param("postId", String.valueOf(postId))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()).andReturn();

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/post/note?postId=" + postId, redirectedUrl);
    }

    @Test
    @WithMockUser(username = "test_user_2")
    public void editReviewHttpRequestWhenUserAndReviewUserAreNotTheSameObject() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        int postId = posts.get(0).getId();
        User user = userService.findByUserName("test_user_1");
        List<Review> reviews = reviewService.findAllByUserId(user.getId());
        Review review = reviews.get(0);
        MvcResult mvcResult = this.mockMvc.perform(post("/post/note/review/edit?postId=" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("reviewId", String.valueOf(review.getId()))
                        .param("id", "1")
                        .param("comment", "The best post!")
                        .param("postId", String.valueOf(postId))
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @WithMockUser(username = "test_admin")
    public void editReviewHttpRequestWhenUserAndReviewUserAreNotTheSameObjectButUserIsAdmin() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        int postId = posts.get(0).getId();
        User user = userService.findByUserName("test_user_1");
        List<Review> reviews = reviewService.findAllByUserId(user.getId());
        Review review = reviews.get(0);
        MvcResult mvcResult = this.mockMvc.perform(post("/post/note/review/edit?postId=" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("reviewId", String.valueOf(review.getId()))
                        .param("id", "1")
                        .param("comment", "The best post!")
                        .param("postId", String.valueOf(postId))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()).andReturn();

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/post/note?postId=" + postId, redirectedUrl);
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void deleteReviewHttpRequest() throws Exception {
        User user = userService.findByUserName("test_user_1");
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        List<Review> reviews = reviewService.findAllByUserId(user.getId());
        Review review = reviews.get(0);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/note/review/delete?postId=" +
                        post.getId() + "&reviewId=" + review.getId()))
                .andExpect(status().is3xxRedirection()).andReturn();

        Review theSameReview = reviewService.findById(review.getId());
        assertNull(theSameReview);

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/post/note?postId=" + post.getId(), redirectedUrl);
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void deleteReviewHttpRequestWhenReviewDoesNotExists() throws Exception {
        User user = userService.findByUserName("test_user_1");
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/note/review/delete?postId=" +
                        post.getId() + "&reviewId=" + 9999))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @WithMockUser(username = "nonexistentUser")
    public void deleteReviewHttpRequestWhenUserIsNotExists() throws Exception {
        User user = userService.findByUserName("test_user_1");
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        List<Review> reviews = reviewService.findAllByUserId(user.getId());
        Review review = reviews.get(0);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/note/review/delete?postId=" +
                        post.getId() + "&reviewId=" + review.getId()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @WithMockUser(username = "test_user_2")
    public void deleteReviewHttpRequestWhenUserAndReviewUserAreNotTheSameObject() throws Exception {
        User user = userService.findByUserName("test_user_1");
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        List<Review> reviews = reviewService.findAllByUserId(user.getId());
        Review review = reviews.get(0);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/note/review/delete?postId=" +
                        post.getId() + "&reviewId=" + review.getId()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @WithMockUser(username = "test_admin")
    public void deleteReviewHttpRequestWhenUserAndReviewUserAreNotTheSameObjectButUserIsAdmin() throws Exception {
        User user = userService.findByUserName("test_user_1");
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        List<Review> reviews = reviewService.findAllByUserId(user.getId());
        Review review = reviews.get(0);
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/post/note/review/delete?postId=" +
                        post.getId() + "&reviewId=" + review.getId()))
                .andExpect(status().is3xxRedirection()).andReturn();

        Review theSameReview = reviewService.findById(review.getId());
        assertNull(theSameReview);

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/post/note?postId=" + post.getId(), redirectedUrl);
    }

    @Test
    @WithMockUser("test_user_1")
    public void addReviewLikeHttpRequest() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        List<Review> reviews = post.getReviews();
        Review review = reviews.get(0);
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/post/note/review/addLike?postId=" +
                        post.getId() + "&reviewId=" + review.getId()))
                .andExpect(status().is3xxRedirection()).andReturn();

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/post/note?postId=" + post.getId(), redirectedUrl);
    }

    @Test
    @WithMockUser("test_user_1")
    public void addReviewLikeHttpRequestWhenReviewIsNotExists() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/post/note/review/addLike?postId=" +
                        post.getId() + "&reviewId=" + 999))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @WithMockUser("nonexistentUser")
    public void addReviewLikeHttpRequestWhenUserIsNotExists() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        List<Review> reviews = post.getReviews();
        Review review = reviews.get(0);
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/post/note/review/addLike?postId=" +
                        post.getId() + "&reviewId=" + review.getId()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @WithMockUser("test_user_1")
    public void addReviewLikeHttpRequestWithParamDoesNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/post/note/review/addLike?gender=male"))
                .andExpect(status().is4xxClientError()).andReturn();
    }

    @Test
    @WithMockUser("test_user_1")
    public void removeReviewLikeHttpRequestWhenUserNotLikedReview() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        List<Review> reviews = post.getReviews();
        Review review = reviews.get(0);
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/post/note/review/removeLike?postId=" +
                                post.getId() + "&reviewId=" + review.getId()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @WithMockUser("test_user_2")
    public void removeReviewLikeHttpRequestWhenUserLikedReview() throws Exception {
        User user = userService.findByUserName("test_user_2");
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        List<Review> reviews = post.getReviews();
        Review review = reviews.get(0);
        reviewLikeDao.addLike(review, user);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/post/note/review/removeLike?postId=" +
                                post.getId() + "&reviewId=" + review.getId()))
                .andExpect(status().is3xxRedirection()).andReturn();

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/post/note?postId=" + post.getId(), redirectedUrl);
    }

    @Test
    @WithMockUser("test_user_1")
    public void removeReviewLikeHttpRequestWhenReviewIsNotExists() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/post/note/review/removeLike?postId=" +
                                post.getId() + "&reviewId=" + 999))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @WithMockUser("nonexistentUser")
    public void removeReviewLikeHttpRequestWhenUserIsNotExists() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        List<Review> reviews = post.getReviews();
        Review review = reviews.get(0);
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/post/note/review/removeLike?postId=" +
                                post.getId() + "&reviewId=" + review.getId()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @WithMockUser("test_user_1")
    public void removeReviewLikeHttpRequestWithParamDoesNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/post/note/review/removeLike?gender=male"))
                .andExpect(status().is4xxClientError()).andReturn();
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

        roleDao.deleteByName("ROLE_USER");
        roleDao.deleteByName("ROLE_ADMIN");
    }
}
