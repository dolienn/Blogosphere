package pl.dolien.freetube.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dolien.freetube.dao.RoleDao;
import pl.dolien.freetube.dao.UserDao;
import pl.dolien.freetube.dao.UserRepository;
import pl.dolien.freetube.entity.Review;
import pl.dolien.freetube.entity.Role;
import pl.dolien.freetube.entity.User;
import pl.dolien.freetube.entity.Post;
import pl.dolien.freetube.validation.WebUser;

import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private UserDao userDao;

	private UserRepository userRepository;

	private RoleDao roleDao;

	private BCryptPasswordEncoder passwordEncoder;

	private ReviewService reviewService;

	@Autowired
	public UserServiceImpl(UserDao userDao, RoleDao roleDao, BCryptPasswordEncoder passwordEncoder) {
		this.userDao = userDao;
		this.roleDao = roleDao;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User findByUserName(String userName) {
		return userDao.findByUserName(userName);
	}

	@Override
	public User findByEmail(String email) {
		return userDao.findByEmail(email);
	}

	@Override
	public List<User> findAll() {
		return userDao.getAllUsers();
	}

	@Override
	public boolean checkIfUserIsNull(String userName) {
		Optional<User> user = Optional.ofNullable(userDao.findByUserName(userName));
		return user.isPresent();
	}

	@Override
	public void save(WebUser webUser) {
		User user = new User();

		// assign user details to the user object
		user.setUserName(webUser.getUserName());
		user.setPassword(passwordEncoder.encode(webUser.getPassword()));
		user.setFirstName(webUser.getFirstName());
		user.setLastName(webUser.getLastName());
		user.setEmail(webUser.getEmail());
		user.setEnabled(true);

		// give user default role of "employee"
		user.setRoles(Arrays.asList(roleDao.findRoleByName("ROLE_EMPLOYEE")));

		// save user in the database
		userDao.save(user);
	}

	@Override
	public void createUser(String userName, String password, String firstName, String lastName, String email, Collection<Role> roles, List<Post> posts, List<Review> reviews) {
		User user = new User(userName, password, true, firstName, lastName, email, roles, posts, reviews);
		user.setId(0L);
		userDao.save(user);
	}

	@Override
	public void deleteUser(String userName) {
		User user = userDao.findByUserName(userName);
		user.setRoles(null);

		List<Post> posts = user.getPosts();

		for (Post post : posts) {
			post.setUser(null);
		}

		userDao.delete(user);
	}

	@Override
	public List<User> findAllByAsc() {
		return userRepository.findAllByOrderByUserNameAsc();
	}

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		User user = userDao.findByUserName(userName);

		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}

		Collection<SimpleGrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());

		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
				authorities);
	}

	private Collection<SimpleGrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

		for (Role tempRole : roles) {
			SimpleGrantedAuthority tempAuthority = new SimpleGrantedAuthority(tempRole.getName());
			authorities.add(tempAuthority);
		}

		return authorities;
	}

}
