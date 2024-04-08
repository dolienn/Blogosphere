package pl.dolien.blogosphere.controller;

import org.junit.jupiter.api.Test;
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
import pl.dolien.blogosphere.service.UserService;
import pl.dolien.blogosphere.validation.WebUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    public void registerHttpRequest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/register"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "register/registration");
    }

    @Test
    public void processRegistrationFormHttpRequest() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userName", "test_user_5")
                        .param("password", "test123")
                        .param("firstName", "John")
                        .param("lastName", "Tester")
                        .param("email", "test@example.com")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "register/registration-confirmation");
    }

    @Test
    public void processRegistrationFormHttpRequestWithInvalidValidation() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userName", "abc") // Username must have min 4 characters
                        .param("password", "test123")
                        .param("firstName", "John")
                        .param("lastName", "Tester")
                        .param("email", "test@example.com")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "register/registration");
    }

    @Test
    public void processRegistrationFormHttpRequestWhenUsernameIsAlreadyExists() throws Exception {
        WebUser webUser1 = WebUser.builder()
                .userName("test_user_32")
                .password("test123")
                .email("test1@example.com")
                .lastName("Tester")
                .firstName("John")
                .build();

        userService.save(webUser1);

        MvcResult mvcResult = this.mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userName", "test_user_32")
                        .param("password", "test123")
                        .param("firstName", "John")
                        .param("lastName", "Tester")
                        .param("email", "test@example.com")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        assert mav != null;
        ModelAndViewAssert.assertViewName(mav, "register/registration");
    }
}
