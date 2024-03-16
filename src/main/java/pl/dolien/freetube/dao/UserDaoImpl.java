package pl.dolien.freetube.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.dolien.freetube.entity.Post;
import pl.dolien.freetube.entity.User;

@Repository
public class UserDaoImpl implements UserDao {

	private EntityManager entityManager;

	@Autowired
	public UserDaoImpl(EntityManager theEntityManager) {
		this.entityManager = theEntityManager;
	}

	@Override
	public Iterable<User> getAllUsers() {
		TypedQuery<User> theQuery = entityManager.createQuery("from User", User.class);
		return theQuery.getResultList();
	}

	@Override
	public User findByUserName(String theUserName) {

		// retrieve/read from database using username
		TypedQuery<User> theQuery = entityManager.createQuery("from User where userName=:uName and enabled=true", User.class);
		theQuery.setParameter("uName", theUserName);

		User theUser = null;
		try {
			theUser = theQuery.getSingleResult();
		} catch (Exception ignored) {
			// TO DO
        }

		return theUser;
	}

	@Override
	public User findByEmail(String email) {
		TypedQuery<User> theQuery = entityManager.createQuery("from User email=:uEmail and enabled=true", User.class);
		theQuery.setParameter("uEmail", email);

		User user = null;
		try {
			user = theQuery.getSingleResult();
		} catch (Exception ignored) {
			// TO DO
        }

		return user;
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
