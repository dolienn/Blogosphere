package pl.dolien.freetube.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import pl.dolien.freetube.dao.RoleDao;
import pl.dolien.freetube.entity.Post;
import pl.dolien.freetube.entity.Review;
import pl.dolien.freetube.entity.Role;
import pl.dolien.freetube.entity.User;
import pl.dolien.freetube.validation.WebUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@SpringBootTest
public class PostServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void placeholder() {

        WebUser user = WebUser.builder()
                .userName("tester")
                .email("tester@test.com")
                .firstName("Alfa")
                .lastName("Beta")
                .password("fun123").build();

        userService.save(user);

        List<User> users = userService.findAll();

        assertEquals(1, users.size());
    }
}
