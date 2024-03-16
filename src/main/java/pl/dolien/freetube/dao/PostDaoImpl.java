package pl.dolien.freetube.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.dolien.freetube.entity.Post;
import pl.dolien.freetube.entity.Review;

import java.util.List;

@Repository
public class PostDaoImpl implements PostDao {

    private final EntityManager entityManager;

    @Autowired
    public PostDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Iterable<Post> getAllPost() {
        TypedQuery<Post> theQuery = entityManager.createQuery("from Post", Post.class);
        return theQuery.getResultList();
    }

    @Override
    public Iterable<Post> findPostByUsername(String userName) {
        TypedQuery<Post> theQuery = entityManager.createQuery("from Post where user.userName=:uName and user.enabled=true", Post.class);
        theQuery.setParameter("uName", userName);

        Iterable<Post> post = null;
        try {
            post = theQuery.getResultList();
        } catch (Exception ignored) {
            // TO DO
        }

        return post;
    }

    @Override
    public Post findPostById(int id) {
        return entityManager.find(Post.class, id);
    }

    @Override
    public Post findPostByTitle(String title) {
        TypedQuery<Post> theQuery = entityManager.createQuery("from Post where title=:postTitle", Post.class);
        theQuery.setParameter("postTitle", title);

        Post post = null;
        try {
            post = theQuery.getSingleResult();
        } catch (Exception ignored) {
            // TO DO
        }

        return post;
    }

    @Override
    @Transactional
    public void save(Post post) {
        entityManager.merge(post);
    }

    @Override
    @Transactional
    public void delete(Post post) {
        entityManager.remove(post);
    }

}
