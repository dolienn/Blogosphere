package pl.dolien.freetube.dao;


import pl.dolien.freetube.entity.Role;

public interface RoleDao {

	Role findRoleByName(String theRoleName);
	
}
