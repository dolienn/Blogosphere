package pl.dolien.freetube.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dolien.freetube.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAllByOrderByUserNameAsc();
}
