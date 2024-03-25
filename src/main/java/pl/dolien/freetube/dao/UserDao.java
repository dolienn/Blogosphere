package pl.dolien.freetube.dao;

import pl.dolien.freetube.entity.User;

import java.util.List;

public interface UserDao {

    List<User> getAllUsers();

    User findByUserName(String userName);

    User findByEmail(String email);

    void save(User theUser);

    void delete(User theUser);

}
