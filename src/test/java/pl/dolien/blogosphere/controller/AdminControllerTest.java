package pl.dolien.blogosphere.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Mock
    private UserService userServiceMock;

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

        Role role = new Role("ROLE_USER");
        Role role2 = new Role("ROLE_ADMIN");
        roleDao.save(role);
        roleDao.save(role2);

        userService.save(webUser1);

        WebUser webUser2 = WebUser.builder()
                .userName("test_user_2")
                .password("test123")
                .email("test2@example.com")
                .lastName("Tester")
                .firstName("Susan")
                .build();

        userService.save(webUser2);

        WebUser webUser3 = WebUser.builder()
                .userName("test_admin")
                .password("test123")
                .email("test3@example.com")
                .lastName("Tester")
                .firstName("Harry")
                .roles(Arrays.asList(roleDao.findRoleByName("ROLE_ADMIN")))
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
    @WithMockUser(username = "test_admin", roles = {"ADMIN"})
    public void usersHttpRequestWhenUserIsAdmin() throws Exception {
        User user = new User(
                "test_user_4", "test123",
                true, "Tod", "Tester",
                "test4@example.com", Collections.singletonList(roleDao.findRoleByName("ROLE_USER")),
                null, null);

        when(userServiceMock.findAll()).thenReturn(List.of(user));
        assertIterableEquals(List.of(user), userServiceMock.findAll());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "/admin/users");
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void usersHttpRequestWhenUserIsNotAdmin() throws Exception {
        User user = new User(
                "test_user_4", "test123",
                true, "Tod", "Tester",
                "test4@example.com", Collections.singletonList(roleDao.findRoleByName("ROLE_USER")),
                null, null);

        when(userServiceMock.findAll()).thenReturn(List.of(user));
        assertIterableEquals(List.of(user), userServiceMock.findAll());

        mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test_admin", roles = {"ADMIN"})
    public void deleteUserHttpRequestWhenUserIsAdmin() throws Exception {
        User user = userService.findByUserName("test_user_1");
        assertNotNull(user);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/admin/user/delete?theUserName=" + user.getUserName()))
                .andExpect(status().is3xxRedirection()).andReturn();

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/admin/users", redirectedUrl);
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void deleteUserHttpRequestWhenUserIsNotAdmin() throws Exception {
        User user = userService.findByUserName("test_user_1");
        assertNotNull(user);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/admin/user/delete?theUserName=" + user.getUserName()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test_admin", roles = {"ADMIN"})
    public void deletePostHttpRequestWhenUserIsAdmin() throws Exception {
        User user = userService.findByUserName("test_user_2");
        assertNotNull(user);

        List<Post> posts = postService.findPostsByTitle("The Lord");
        Post post = posts.get(0);
        assertNotNull(post);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/admin/post/delete?theUserName=" +
                                user.getUserName() + "&postId=" + post.getId()))
                .andExpect(status().is3xxRedirection()).andReturn();

        assertNull(postService.findById(post.getId()));

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/admin/user-posts?theUserName=" + user.getUserName(), redirectedUrl);
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void deletePostHttpRequestWhenUserIsNotAdmin() throws Exception {
        User user = userService.findByUserName("test_user_2");
        assertNotNull(user);

        List<Post> posts = postService.findPostsByTitle("The Lord");
        Post post = posts.get(0);
        assertNotNull(post);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/admin/post/delete?theUserName=" +
                                user.getUserName() + "&postId=" + post.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test_admin", roles = {"ADMIN"})
    public void deleteReviewHttpRequestWhenUserIsAdmin() throws Exception {
        User user = userService.findByUserName("test_user_1");
        assertNotNull(user);

        List<Review> reviews = user.getReviews();
        Review review = reviews.get(0);
        assertNotNull(review);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/admin/review/delete?theUserName=" +
                                user.getUserName() + "&reviewId=" + review.getId()))
                .andExpect(status().is3xxRedirection()).andReturn();

        assertNull(reviewService.findById(review.getId()));

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/admin/user-reviews?theUserName=" + user.getUserName(), redirectedUrl);
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void deleteReviewHttpRequestWhenUserIsNotAdmin() throws Exception {
        User user = userService.findByUserName("test_user_1");
        assertNotNull(user);

        List<Review> reviews = user.getReviews();
        Review review = reviews.get(0);
        assertNotNull(review);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/admin/review/delete?theUserName=" +
                                user.getUserName() + "&reviewId=" + review.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test_admin", roles = {"ADMIN"})
    public void userPostsHttpRequestWhenUserIsAdmin() throws Exception {
        User user = userService.findByUserName("test_user_2");
        assertNotNull(user);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/admin/user-posts?theUserName=" + user.getUserName()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "/admin/posts");
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void userPostsHttpRequestWhenUserIsNotAdmin() throws Exception {
        User user = userService.findByUserName("test_user_2");
        assertNotNull(user);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/admin/user-posts?theUserName=" + user.getUserName()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test_admin", roles = {"ADMIN"})
    public void userReviewsHttpRequestWhenUserIsAdmin() throws Exception {
        User user = userService.findByUserName("test_user_2");
        assertNotNull(user);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/admin/user-reviews?theUserName=" + user.getUserName()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "/admin/reviews");
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void userReviewsHttpRequestWhenUserIsNotAdmin() throws Exception {
        User user = userService.findByUserName("test_user_2");
        assertNotNull(user);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/admin/user-reviews?theUserName=" + user.getUserName()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test_admin", roles = {"ADMIN"})
    public void postReviewsHttpRequestWhenUserIsAdmin() throws Exception {
        User user = userService.findByUserName("test_user_2");
        assertNotNull(user);

        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/admin/user-reviews?postId=" + post.getId() + "&theUserName=" + user.getUserName()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "/admin/reviews");
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void postReviewsHttpRequestWhenUserIsNotAdmin() throws Exception {
        User user = userService.findByUserName("test_user_2");
        assertNotNull(user);

        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/admin/user-reviews?postId=" +
                        post.getId() + "&theUserName=" + user.getUserName()))
                .andExpect(status().isForbidden());
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
