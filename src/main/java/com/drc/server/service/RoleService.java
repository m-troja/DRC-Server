package com.drc.server.service;

import com.drc.server.entity.Role;

public interface RoleService {

    public String ROLE_USER = "ROLE_USER";
    public String ROLE_ADMIN = "ROLE_ADMIN";
    public String ROLE_CHEATER = "ROLE_CHEATER";

    Role getRoleByName(String roleName);
    void save(Role role);
    void createRoles();
}
