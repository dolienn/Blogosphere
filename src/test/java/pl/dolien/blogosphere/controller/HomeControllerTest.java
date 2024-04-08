package pl.dolien.blogosphere.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;
import pl.dolien.blogosphere.entity.Post;
import pl.dolien.blogosphere.entity.User;
import pl.dolien.blogosphere.service.PostService;
import pl.dolien.blogosphere.service.UserService;
import pl.dolien.blogosphere.validation.WebPost;
import pl.dolien.blogosphere.validation.WebUser;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PostService postServiceMock;

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
    public void showLandingHttpRequest() throws Exception {
        List<Post> posts = Arrays.asList(
                new Post("Harry Potter",
                        "Welcome to my post",
                        "Harry Potter book is very popular among people and i love it",
                        "public"),
                new Post("The Lord of the Rings",
                        "An epic fantasy novel",
                        "The Lord of the Rings is a classic fantasy novel written by J.R.R. Tolkien.",
                        "private")
        );

        when(postServiceMock.findAll()).thenReturn(posts);
        assertIterableEquals(posts, postServiceMock.findAll());


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"landing");
    }

    @Test
    public void showLandingHttpRequestWithPaginationSuccess() throws Exception {
        List<Post> posts = Arrays.asList(
                new Post("Harry Potter",
                        "Welcome to my post",
                        "Harry Potter book is very popular among people and i love it",
                        "public"),
                new Post("The Lord of the Rings",
                        "An epic fantasy novel",
                        "The Lord of the Rings is a classic fantasy novel written by J.R.R. Tolkien.",
                        "private")
        );

        when(postServiceMock.findAll()).thenReturn(posts);
        assertIterableEquals(posts, postServiceMock.findAll());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/?page=0"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"landing");
    }

    @Test
    public void showLandingHttpRequestWithPageDoesNotExists() throws Exception {
        List<Post> posts = Arrays.asList(
                new Post("Harry Potter",
                        "Welcome to my post",
                        "Harry Potter book is very popular among people and i love it",
                        "public"),
                new Post("The Lord of the Rings",
                        "An epic fantasy novel",
                        "The Lord of the Rings is a classic fantasy novel written by J.R.R. Tolkien.",
                        "private")
        );

        when(postServiceMock.findAll()).thenReturn(posts);
        assertIterableEquals(posts, postServiceMock.findAll());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/?page=999"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    public void showLandingHttpRequestWithSearchExists() throws Exception {
        List<Post> posts = List.of(
                new Post("Harry Potter",
                        "Welcome to my post",
                        "Harry Potter book is very popular among people and i love it",
                        "public")
        );

        when(postServiceMock.findPostsByTitle("Harry")).thenReturn(posts);
        assertIterableEquals(posts, postServiceMock.findPostsByTitle("Harry"));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/?search=Harry"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"landing");
    }

    @Test
    public void showLandingHttpRequestWithParamDoesNotExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/?gender=male"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    public void showLandingHttpRequestWithSearchAndPageExist() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/?search=Harry&page=0"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"landing");
    }

    @Test
    public void showLandingHttpRequestWithSearchExistsAndPageNotFound() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/?search=Harry&page=1"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @Test
    public void searchByTitleWhenTitleIsNotEmpty() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("search", "Harry")
                        .with(csrf()))
                        .andExpect(status().is3xxRedirection()).andReturn();

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/?search=Harry", redirectedUrl);
        assert redirectedUrl != null;
        MvcResult redirectedResult = this.mockMvc.perform(get(redirectedUrl))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mav = redirectedResult.getModelAndView();
        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"landing");
    }

    @Test
    public void searchByTitleWhenTitleIsEmpty() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("search", "")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()).andReturn();

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertEquals("/", redirectedUrl);
        assert redirectedUrl != null;
        MvcResult redirectedResult = this.mockMvc.perform(get(redirectedUrl))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mav = redirectedResult.getModelAndView();
        assert mav != null;
        ModelAndViewAssert.assertViewName(mav,"landing");
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
