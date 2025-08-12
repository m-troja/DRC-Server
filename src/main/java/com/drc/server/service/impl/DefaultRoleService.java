package com.drc.server.service.impl;

import com.drc.server.entity.Role;
import com.drc.server.persistence.RoleRepo;
import com.drc.server.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class DefaultRoleService implements RoleService {

    private final RoleRepo roleRepo;

    public Role getRoleByName(String roleName) {
        return roleRepo.findByName(roleName);
    }

    public DefaultRoleService(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    public void save(Role role) {
        roleRepo.save(role);
    }

    public void createRoles() {
        if (roleRepo.findByName(RoleService.ROLE_USER) == null) {
            Role userRole = new Role(RoleService.ROLE_USER);
            save(userRole);
        }
        if (roleRepo.findByName(RoleService.ROLE_ADMIN) == null) {
            Role adminRole = new Role(RoleService.ROLE_ADMIN);
            save(adminRole);
        }
        if (roleRepo.findByName(RoleService.ROLE_CHEATER) == null) {
            Role cheaterRole = new Role(RoleService.ROLE_CHEATER);
            save(cheaterRole);
        }
    }
}

