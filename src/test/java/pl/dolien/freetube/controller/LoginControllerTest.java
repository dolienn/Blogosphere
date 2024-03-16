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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.ModelAndView;
import pl.dolien.freetube.service.UserService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Mock
    UserService userServiceMock;

    @Test
    public void showLoginHttpRequest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/showMyLoginPage"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "/login/login");
    }

    @Test
    @WithMockUser
    public void showAccessDeniedHttpRequestWithAuthenticatedAccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/showMyLoginPage"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "/login/login");
    }


    @Test
    public void showAccessDeniedHttpRequestWithoutAuthenticatedAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/access-denied"))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/showMyLoginPage"));
    }
}
