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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Mock
    UserService userServiceMock;

    @Test
    public void showLandingHttpRequest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "landing");
    }

    @Test
    @WithMockUser
    public void showHomeHttpRequestWithAuthenticatedAccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/employees"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "home");
    }

    @Test
    public void showHomeHttpRequestWithUnauthenticatedAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/employees"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/showMyLoginPage"));
    }
}
