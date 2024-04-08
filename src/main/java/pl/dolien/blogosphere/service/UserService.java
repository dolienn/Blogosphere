package pl.dolien.blogosphere.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import pl.dolien.blogosphere.entity.Post;
import pl.dolien.blogosphere.entity.Review;
import pl.dolien.blogosphere.entity.Role;
import pl.dolien.blogosphere.entity.User;
import pl.dolien.blogosphere.validation.WebUser;

import java.util.Collection;
import java.util.List;

public interface UserService extends UserDetailsService {

	User findByUserName(String userName);

	List<User> findAll();

	boolean checkIfUserIsNull(String userName);

	void save(WebUser webUser);

	void createUser(String userName, String password, String firstName, String lastName, String email, Collection<Role> roles, List<Post> posts, List<Review> reviews);

	void deleteUser(String userName);

}
