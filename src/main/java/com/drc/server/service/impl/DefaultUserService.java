package com.drc.server.service.impl;

import com.drc.server.entity.User;
import com.drc.server.persistence.UserRepo;
import com.drc.server.service.RoleService;
import com.drc.server.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Slf4j
@Service
public class DefaultUserService implements UserService {

    private final RoleService roleService;
    private final UserRepo userRepo;

    public boolean save(User user) {
        if (validateNewUser(user)) {
            try {
                userRepo.save(user);
            }
            catch (Exception e) {
                log.debug("Error saving user {}:{}", user.getId(), user.getName());
                return false;
            }
            log.debug("Saved user {}:{}", user.getId(), user.getName());
            return true;
        }
        return false;
    }

    public boolean validateNewUser(User user) {
        String username = user.getName();
        String roleName = user.getRole().getName();
        Double money = user.getMoney();
        boolean isUsernameValid = false;
        boolean isRoleValid = false;
        boolean isMoneyValid = false;

        if (userRepo.findByName(username) == null) {
            isUsernameValid = true;
            log.debug("Username {} OK", username);
        }
        else {
            log.debug("Username {} invalid, could already exist!", username);
        }

        if (user.getRole().getName().equals(RoleService.ROLE_USER)) {
            isRoleValid = true;
            log.debug("Role of user {} OK", username);
        }
        else {
            log.debug("Role of user {} is invalid!", username);
        }

        if (user.getMoney() == 0) {
            isMoneyValid = true;
            log.debug("Money of user {} OK", username);
        }
        else {
            log.debug("Money of user {} is invalid!", username);
        }

        if (isUsernameValid && isRoleValid && isMoneyValid) {
            log.debug("User {} validated!", username);
            return true;
        }
        else {
            log.debug("User {} rejected!", username);
            return false;
        }
    }
}
