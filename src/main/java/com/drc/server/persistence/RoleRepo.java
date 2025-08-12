package com.drc.server.persistence;

import com.drc.server.entity.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepo extends CrudRepository<Role, Integer> {

    public Role findByName(String roleName);
}
