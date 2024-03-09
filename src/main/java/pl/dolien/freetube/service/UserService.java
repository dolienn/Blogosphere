package pl.dolien.freetube.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import pl.dolien.freetube.entity.Role;
import pl.dolien.freetube.entity.User;
import pl.dolien.freetube.user.WebUser;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

	User findByUserName(String userName);

	User findByEmail(String email);

	Iterable<User> findAll();

	boolean checkIfUserIsNull(String userName);

	void save(WebUser webUser);

	void createUser(String userName, String password, String firstName, String lastName, String email, Collection<Role> roles);

	void deleteUser(String userName);
}
