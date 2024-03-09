package pl.dolien.freetube.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;
import pl.dolien.freetube.dao.RoleDao;
import pl.dolien.freetube.dao.UserDao;
import pl.dolien.freetube.entity.Role;
import pl.dolien.freetube.entity.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application.properties")
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private JdbcTemplate jdbc;

//    @Value("${sql.script.create.user}")
//    private String sqlAddUser;

//    @Value("${sql.script.create.role.employee}")
//    private String sqlAddRoleEmployee;
//
//    @Value("${sql.script.create.role.manager}")
//    private String sqlAddRoleManager;
//
//    @Value("${sql.script.create.role.admin}")
//    private String sqlAddRoleAdmin;
//
//    @Value("${sql.script.create.user.relation.role}")
//    private String sqlAddRelation;

//    @Value("${sql.script.delete.user}")
//    private String sqlDeleteUser;

//    @Value("${sql.script.delete.role}")
//    private String sqlDeleteRole;
//
//    @Value("${sql.script.delete.relation}")
//    private String sqlDeleteRelation;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleDao roleDao;
    @BeforeEach
    public void createUser() {
        Role userRoles = roleDao.findRoleByName("ROLE_EMPLOYEE");
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(userRoles);
        assertNull(userService.findByUserName("some"));
        userService.createUser("some", "$2a$10$gK6dKQQn6d1iPDk3rSywJ.qSHLcQdCgZeyPjrKapjNRAPLJzfj6Ha", "Some", "Thing", "something@dolien.pl", roles);
        User user = userService.findByUserName("some");

        assertEquals("something@dolien.pl", user.getEmail(), "Lines should matches");
    }

    @Test
    public void findAllUsers() {
        assertNotNull(userService.findAll(), "Should be not null");
    }

    @Test
    public void isUserNullCheck() {
        assertTrue(userService.checkIfUserIsNull("some"), "Should be true");
        assertFalse(userService.checkIfUserIsNull("a"), "Should be false");
    }

    @Test
    public void checkIfLoadUserByUsernameIsNotNull() {
        assertNotNull(userService.loadUserByUsername("some"));
        assertThrows(UsernameNotFoundException.class,
                () -> { userService.loadUserByUsername("a"); }, "Should throw exception");
    }

    @AfterEach
    public void deleteUser() {
        assertNotNull(userService.findByUserName("some"));
        userService.deleteUser("some");
    }
}
