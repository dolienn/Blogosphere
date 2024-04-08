package pl.dolien.blogosphere.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.dolien.blogosphere.entity.User;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

	private final EntityManager entityManager;

	@Autowired
	public UserDaoImpl(EntityManager theEntityManager) {
		this.entityManager = theEntityManager;
	}

	@Override
	public List<User> getAllUsers() {
		TypedQuery<User> theQuery = entityManager.createQuery("from User", User.class);
		return theQuery.getResultList();
	}

	@Override
	public User findByUserName(String theUserName) {

		TypedQuery<User> theQuery = entityManager.createQuery("from User where userName=:uName and enabled=true", User.class);
		theQuery.setParameter("uName", theUserName);

		User theUser = null;
		try {
			theUser = theQuery.getSingleResult();
		} catch (Exception ignored) {

        }

		return theUser;
	}

	@Override
	@Transactional
	public void save(User theUser) {
		entityManager.merge(theUser);
	}

	@Override
	@Transactional
	public void delete(User theUser) {
		entityManager.remove(theUser);
	}

}
