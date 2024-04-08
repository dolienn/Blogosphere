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
import pl.dolien.blogosphere.dao.PostLikeDao;
import pl.dolien.blogosphere.dao.RoleDao;
import pl.dolien.blogosphere.entity.Post;
import pl.dolien.blogosphere.entity.Role;
import pl.dolien.blogosphere.entity.User;
import pl.dolien.blogosphere.service.PostService;
import pl.dolien.blogosphere.service.UserService;
import pl.dolien.blogosphere.validation.WebPost;
import pl.dolien.blogosphere.validation.WebUser;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PostLikeDao postLikeDao;

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
                .description("The best")
                .note("Harry Potter book is very popular among people and i love it")
                .privacy("public")
                .user(user)
                .build();

        WebPost webPost2 = WebPost.builder()
                .title("The Lord")
                .description("An epic")
                .note("The Lord of the Rings is a classic fantasy novel written by J.R.R. Tolkien.")
                .privacy("private")
                .user(user2)
                .build();

        postService.save(webPost1);
        postService.save(webPost2);
    }

    @Test
    @WithMockUser
    public void showUploadPostPageHttpRequestWithAuthenticatedAccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/upload"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "upload-post");
    }

    @Test
    public void showUploadPostPageHttpRequestWithoutAuthenticatedAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/post/upload"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser
    public void uploadPostHttpRequestWithCorrectValidation() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/post/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id", "1")
                        .param("title", "The Lord")
                        .param("description", "An epic fantasy")
                        .param("note", "The Lord of the Rings is a classic fantasy novel written by J.R.R. Tolkien.")
                        .param("privacy", "public")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()).andReturn();

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/", redirectedUrl);
    }

    @Test
    @WithMockUser
    public void uploadPostHttpRequestWithInvalidValidation() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/post/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id", "1")
                        .param("title", "The Lord")
                        .param("description", "An epic fantasy")
                        .param("note", "hello") //min 10 characters so validation is invalid
                        .param("privacy", "public")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "upload-post");
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void showNoteHttpRequestWithPostIdExists() throws Exception {


        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        int postId = posts.get(0).getId();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/note?postId=" + postId))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"post");
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void showNoteHttpRequestWithPostIdAndPageExist() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        int postId = posts.get(0).getId();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/note?postId=" + postId + "&page=0"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"post");
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void showNoteHttpRequestWithPageDoesNotExists() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        int postId = posts.get(0).getId();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/note?postId=" + postId + "&page=1"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void showNoteHttpRequestWithParamDoesNotExists() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        int postId = posts.get(0).getId();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/note?postId=" + postId + "&not=1"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void showNoteHttpRequestWithPostDoesNotExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/note?postId=" + 999))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser(username = "nonexistentUser")
    public void showNoteHttpRequestWithUserDoesNotExists() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        int postId = posts.get(0).getId();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/note?postId=" + postId))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void showNoteHttpRequestWithUserAndPostUserAreNotTheSameObjectAndPostIsPrivate() throws Exception {
        List<Post> posts = postService.findPostsByTitle("The Lord");
        int postId = posts.get(0).getId();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/note?postId=" + postId))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser(username = "test_admin")
    public void showNoteHttpRequestWithUserAndPostUserAreNotTheSameObjectAndPostIsPrivateButUserIsAdmin() throws Exception {
        List<Post> posts = postService.findPostsByTitle("The Lord");
        int postId = posts.get(0).getId();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/note?postId=" + postId))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"post");
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void addReviewHttpRequest() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        int postId = posts.get(0).getId();
        MvcResult mvcResult = this.mockMvc.perform(post("/post/note?postId=" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
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
    public void addReviewHttpRequestWithPostDoesNotExists() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/post/note?postId=" + 999)
                        .contentType(MediaType.APPLICATION_JSON)
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
    public void addReviewHttpRequestWithUserDoesNotExists() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        int postId = posts.get(0).getId();
        MvcResult mvcResult = this.mockMvc.perform(post("/post/note?postId=" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
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
    public void addReviewHttpRequestWithInvalidValidation() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        int postId = posts.get(0).getId();
        MvcResult mvcResult = this.mockMvc.perform(post("/post/note?postId=" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id", "1")
                        .param("comment", "")
                        .param("postId", String.valueOf(postId))
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "post");
    }

    @Test
    @WithMockUser("test_user_1")
    public void myPostsHttpRequest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/my"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "my-posts");
    }

    @Test
    @WithMockUser("nonexistentUser")
    public void myPostsHttpRequestWithUserDoesNotExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/my"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @WithMockUser("test_user_1")
    public void myPostsHttpRequestWithPaginationSuccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/my?page=0"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "my-posts");
    }

    @Test
    @WithMockUser("test_user_1")
    public void myPostsHttpRequestWithPageDoesNotExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/my?page=1"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser("test_user_1")
    public void myPostsHttpRequestWithParamDoesNotExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/my?gender=male"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser("test_user_1")
    public void favoritePostsHttpRequest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/favorite"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "my-posts");
    }

    @Test
    @WithMockUser("nonexistentUser")
    public void favoritePostsHttpRequestWithUserDoesNotExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/favorite"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @WithMockUser("test_user_1")
    public void favoritePostsHttpRequestWithPaginationSuccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/favorite?page=0"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "my-posts");
    }

    @Test
    @WithMockUser("test_user_1")
    public void favoritePostsHttpRequestWithPageDoesNotExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/favorite?page=1"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser("test_user_1")
    public void favoritePostsHttpRequestWithParamDoesNotExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/favorite?gender=male"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser("test_user_1")
    public void editPostHttpRequest() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/edit?postId=" + post.getId()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"upload-post");
    }

    @Test
    @WithMockUser("test_user_1")
    public void editPostHttpRequestWithPostDoesNotExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/edit?postId=" + 999))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser("nonexistentUser")
    public void editPostHttpRequestWithUserDoesNotExists() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/edit?postId=" + post.getId()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void editPostHttpRequestWithUserAndPostUserAreNotTheSameObjectAndPostIsPrivate() throws Exception {
        List<Post> posts = postService.findPostsByTitle("The Lord");
        int postId = posts.get(0).getId();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/edit?postId=" + postId))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser(username = "test_admin")
    public void editPostHttpRequestWithUserAndPostUserAreNotTheSameObjectAndPostIsPrivateButUserIsAdmin() throws Exception {
        List<Post> posts = postService.findPostsByTitle("The Lord");
        int postId = posts.get(0).getId();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/edit?postId=" + postId))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"upload-post");
    }

    @Test
    @WithMockUser("test_user_1")
    public void editPostHttpRequestWithParamDoesNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/post/edit?gender=male"))
                .andExpect(status().is4xxClientError()).andReturn();
    }

    @Test
    @WithMockUser("test_user_1")
    public void deletePostHttpRequest() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/delete?postId=" + post.getId()))
                .andExpect(status().is3xxRedirection()).andReturn();

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/post/my", redirectedUrl);
        assert redirectedUrl != null;
        MvcResult redirectedResult = this.mockMvc.perform(get(redirectedUrl))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mav = redirectedResult.getModelAndView();
        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"my-posts");
    }

    @Test
    @WithMockUser("test_user_1")
    public void deletePostHttpRequestWithPostDoesNotExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/delete?postId=" + 999))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser("nonexistentUser")
    public void deletePostHttpRequestWithUserDoesNotExists() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/delete?postId=" + post.getId()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser(username = "test_user_1")
    public void deletePostHttpRequestWithUserAndPostUserAreNotTheSameObjectAndPostIsPrivate() throws Exception {
        List<Post> posts = postService.findPostsByTitle("The Lord");
        int postId = posts.get(0).getId();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/delete?postId=" + postId))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser(username = "test_admin")
    public void deletePostHttpRequestWithUserAndPostUserAreNotTheSameObjectAndPostIsPrivateButUserIsAdmin() throws Exception {
        List<Post> posts = postService.findPostsByTitle("The Lord");
        int postId = posts.get(0).getId();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/delete?postId=" + postId))
                .andExpect(status().is3xxRedirection()).andReturn();

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/post/my", redirectedUrl);
        assert redirectedUrl != null;
        MvcResult redirectedResult = this.mockMvc.perform(get(redirectedUrl))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mav = redirectedResult.getModelAndView();
        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"my-posts");
    }

    @Test
    @WithMockUser("test_user_1")
    public void deletePostHttpRequestWithParamDoesNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/post/delete?gender=male"))
                .andExpect(status().is4xxClientError()).andReturn();
    }

    @Test
    @WithMockUser("test_user_1")
    public void addPostLikeHttpRequest() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/addLike?postId=" + post.getId()))
                .andExpect(status().is3xxRedirection()).andReturn();

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/post/note?postId=" + post.getId(), redirectedUrl);
    }

    @Test
    @WithMockUser("test_user_2")
    public void addPostLikeHttpRequestWhenUserAlreadyLikedPost() throws Exception {
        User user = userService.findByUserName("test_user_2");
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        postLikeDao.addLike(post, user);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/addLike?postId=" + post.getId()))
                .andExpect(status().is3xxRedirection()).andReturn();

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/post/note?postId=" + post.getId(), redirectedUrl);
    }

    @Test
    @WithMockUser("test_user_1")
    public void addPostLikeHttpRequestWithPostDoesNotExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/addLike?postId=" + 999))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser("nonexistentUser")
    public void addPostLikeHttpRequestWithUserDoesNotExists() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/addLike?postId=" + post.getId()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser("test_user_1")
    public void addPostLikeHttpRequestWithParamDoesNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/post/addLike?gender=male"))
                .andExpect(status().is4xxClientError()).andReturn();
    }

    @Test
    @WithMockUser("test_user_1")
    public void removePostLikeHttpRequestWhenUserNotLikedPost() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/removeLike?postId=" + post.getId()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser("test_user_2")
    public void removePostLikeHttpRequestWhenUserLikedPost() throws Exception {
        User user = userService.findByUserName("test_user_2");
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        postLikeDao.addLike(post, user);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/removeLike?postId=" + post.getId()))
                .andExpect(status().is3xxRedirection()).andReturn();

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/post/note?postId=" + post.getId(), redirectedUrl);
    }

    @Test
    @WithMockUser("test_user_1")
    public void removePostLikeHttpRequestWithPostDoesNotExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/removeLike?postId=" + 999))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser("nonexistentUser")
    public void removePostLikeHttpRequestWithUserDoesNotExists() throws Exception {
        List<Post> posts = postService.findPostsByTitle("Harry Potter");
        Post post = posts.get(0);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/removeLike?postId=" + post.getId()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    @WithMockUser("test_user_1")
    public void removePostLikeHttpRequestWithParamDoesNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/post/removeLike?gender=male"))
                .andExpect(status().is4xxClientError()).andReturn();
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

        roleDao.deleteByName("ROLE_USER");
        roleDao.deleteByName("ROLE_ADMIN");
    }
}
