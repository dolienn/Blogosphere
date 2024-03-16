package pl.dolien.freetube.controller;

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
import pl.dolien.freetube.service.UserService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Mock
    UserService userServiceMock;

    @Test
    @WithMockUser
    public void showUploadHttpRequestWithAuthenticatedAccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/upload"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "upload-post");
    }

    @Test
    public void showUploadHttpRequestWithUnauthenticatedAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/post/upload"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/showMyLoginPage"));
    }

    @Test
    @WithMockUser
    public void deletePostHttpRequestWithAuthenticatedAccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/post/delete")
                        .param("postId", String.valueOf(1)))
                .andExpect(status().isFound()).andReturn();

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertNotNull(redirectedUrl);
        assertTrue(redirectedUrl.endsWith("/post/my"));
    }
}
