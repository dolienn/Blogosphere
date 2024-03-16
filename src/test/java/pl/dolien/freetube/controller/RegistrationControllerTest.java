package pl.dolien.freetube.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.ModelAndView;
import pl.dolien.freetube.entity.User;
import pl.dolien.freetube.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class RegistrationControllerTest {

    private static MockHttpServletRequest request;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userServiceMock;

    @Autowired
    private UserService userService;

    @BeforeAll
    public static void setup() {
        request = new MockHttpServletRequest();
        request.setParameter("userName", "loaall");
        request.setParameter("password", "1234");
        request.setParameter("firstName", "Chaxxd");
        request.setParameter("lastName", "Darxxby");
        request.setParameter("email", "siemaa.darby@luv2code.com");
    }

    @Test
    public void getUsersHttpRequest() throws Exception {
        User user = new User("tomas", "123", true, null, null, null);
        User user2 = new User("eric", "123", true, null, null, null);

        List<User> userList = new ArrayList<>(Arrays.asList(user, user2));

        when(userServiceMock.findAll()).thenReturn(userList);

        assertIterableEquals(userList, userServiceMock.findAll());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/register/showRegistrationForm"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "register/registration-form");
    }

    @Test
    @WithMockUser
    public void createUserHttpRequestWithAuthenticatedAccess() throws Exception {
        User user = new User("tomas", "123", true, null, null, null);

        List<User> userList = new ArrayList<>(Arrays.asList(user));

        when(userServiceMock.findAll()).thenReturn(userList);

        assertIterableEquals(userList, userServiceMock.findAll());

        MvcResult mvcResult = this.mockMvc.perform(post("/register/processRegistrationForm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userName", request.getParameterValues("userName"))
                        .param("password", request.getParameterValues("password"))
                        .param("firstName", request.getParameterValues("firstName"))
                        .param("lastName", request.getParameterValues("lastName"))
                        .param("email", request.getParameterValues("email"))
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "register/registration-confirmation");

        userService.deleteUser("loaall");
        assertFalse(userService.checkIfUserIsNull("loaall"));
    }

    @Test
    public void createUserHttpRequestWithoutAuthenticatedAccess() throws Exception {
        mockMvc.perform(post("/processRegistrationForm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userName", request.getParameterValues("userName"))
                        .param("password", request.getParameterValues("password"))
                        .param("firstName", request.getParameterValues("firstName"))
                        .param("lastName", request.getParameterValues("lastName"))
                        .param("email", request.getParameterValues("email"))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/showMyLoginPage"));
    }


}
