package pl.dolien.blogosphere.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;
import pl.dolien.blogosphere.dao.RoleDao;
import pl.dolien.blogosphere.entity.User;
import pl.dolien.blogosphere.validation.WebUser;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

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

        WebUser webUser2 = WebUser.builder()
                .userName("test_user_2")
                .password("abc456")
                .email("test2@example.com")
                .lastName("Tester")
                .firstName("Harry")
                .build();

        userService.save(webUser1);
        userService.save(webUser2);
    }

    @Test
    public void findAllUsers() {
        List<User> users = userService.findAll();
        assertEquals(2, users.size());
        assertNotEquals(3, users.size());
    }

    @Test
    public void findUserByUsernameWhenUsernameExists() {
        User user = userService.findByUserName("test_user_1");
        assertEquals("test1@example.com", user.getEmail());
        assertNotEquals("test2@example.com", user.getEmail());
    }

    @Test
    public void findUserByUsernameWhenUsernameNotFound() {
        User user = userService.findByUserName("nonexistentUser");
        assertNull(user);
    }

    @Test
    public void createUserWithoutValidationAndDeleteHim() {
        userService.createUser("t", "e", "s", "t", "1",
                Arrays.asList(roleDao.findRoleByName("ROLE_USER")), null, null);
        User user = userService.findByUserName("t");
        assertEquals("1", user.getEmail());

        userService.deleteUser("t");
        User deletedUser = userService.findByUserName("t");
        assertNull(deletedUser);
    }

    @Test
    public void loadAuthorizedUserByUsernameWhenUserExists() {
        UserDetails userDetails = userService.loadUserByUsername("test_user_1");
        assertEquals("test_user_1", userDetails.getUsername());
    }

    @Test
    public void loadAuthorizedUserByUsernameWhenUserNotFound() {
        assertThrows(UsernameNotFoundException.class,
                () -> { userService.loadUserByUsername("nonexistentUser"); });
    }

    @AfterEach
    public void delete() {
        List<User> users = userService.findAll();
        for (User user : users) {
            userService.deleteUser(user.getUserName());
        }
    }
}
