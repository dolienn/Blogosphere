package pl.dolien.blogosphere.dao;

import pl.dolien.blogosphere.entity.Role;

public interface RoleDao {

	Role findRoleByName(String theRoleName);

	void save(Role role);

	void deleteByName(String theRoleName);
}
