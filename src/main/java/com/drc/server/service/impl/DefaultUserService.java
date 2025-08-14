package com.drc.server.service.impl;

import com.drc.server.entity.User;
import com.drc.server.persistence.UserRepo;
import com.drc.server.service.RoleService;
import com.drc.server.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Slf4j
@Service
public class DefaultUserService implements UserService {

    private final RoleService roleService;
    private final UserRepo userRepo;

    public String save(User user) {
        String userValidationResult = validateNewUser(user);
        if (userValidationResult.equals(UserService.VALIDATE_OK)) {
            try {
                userRepo.save(user);
            }
            catch (Exception e) {
                log.debug("Error saving user {}:{}", user.getId(), user.getName());
                return "Error saving user " + user.getId();
            }
            log.debug("Saved user {}:{}", user.getId(), user.getName());
            return UserService.VALIDATE_OK;
        }
        else {
            log.debug("Save user: error saving user: {}, error: {}", user, userValidationResult);
            return userValidationResult;
        }
    }

    public String validateNewUser(User user) {
        String username = user.getName();
        String roleName = user.getRole().getName();
        Double money = user.getMoney();
        boolean isUsernameValid = false;
        boolean isRoleValid = false;
        boolean isMoneyValid = false;
        boolean isSessionidValid = false;

        if (userRepo.findBySessionid(user.getSessionid()) == null) {
            log.debug("SessionID for user {} OK", username);
            isSessionidValid = true;
        }
        else {
            log.debug("SessionID for user {} invalid, could already exist!", username);
        }

        if (userRepo.findByName(username) == null) {
            log.debug("Username {} OK", username);
            isUsernameValid = true;
        }
        else {
            log.debug("Username {} invalid, could already exist!", username);
        }

        if (user.getRole().getName().equals(RoleService.ROLE_USER)) {
            log.debug("Role of user {} OK", username);
            isRoleValid = true;
        }
        else {
            log.debug("Role of user {} is invalid!", username);
        }

        if (user.getMoney() == 0) {
            log.debug("Money of user {} OK", username);
            isMoneyValid = true;
        }
        else {
            log.debug("Money of user {} is invalid!", username);
        }

        StringBuilder errorMessage = new StringBuilder("");
        if (!isSessionidValid) {
            errorMessage.append("Invalid session");
        }
        if (!isUsernameValid) {
            errorMessage.append("Invalid username");
        }
        if (!isMoneyValid) {
            errorMessage.append("Invalid role");
        }
        if (!isRoleValid) {
            errorMessage.append("Invalid money");
        }

        if ( isSessionidValid && isUsernameValid && isRoleValid && isMoneyValid) {
            log.debug("Validation of user {} OK", username);
            return UserService.VALIDATE_OK;
        }
        else {
            log.debug("Validation of user {} failed", username);
            log.debug("Sending error message {}", errorMessage);
            return errorMessage.toString();
        }
    }

    public void delete(User user) {
        try {
            log.debug("Trying to delete user {}", user);
            userRepo.delete(user);
            log.debug("User {} deleted", user);
        }
        catch (Exception e) {
            log.debug("Error deleting user {}: {}", user, e);
        }
    }

    public User getUserBySesssionid(String sessionid) {
        return userRepo.findBySessionid(sessionid);
    }
}
