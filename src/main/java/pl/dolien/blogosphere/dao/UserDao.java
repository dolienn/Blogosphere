package pl.dolien.blogosphere.dao;

import pl.dolien.blogosphere.entity.User;

import java.util.List;

public interface UserDao {

    List<User> getAllUsers();

    User findByUserName(String userName);

    void save(User theUser);

    void delete(User theUser);

}
