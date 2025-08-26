package com.drc.server.service.impl;

import com.drc.server.entity.User;
import com.drc.server.persistence.UserRepo;
import com.drc.server.service.RoleService;
import com.drc.server.service.UserRegistrationService;
import com.drc.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultUserRegistrationService implements UserRegistrationService {

    private final UserRepo userRepo;

    public String validateNewUser(User user) {
        StringBuilder errorMessage = new StringBuilder();
        String username = user.getName();

        if (userRepo.findByhttpSessionId(user.getHttpSessionId()) != null) {
            log.debug("SessionID for user {} invalid, could already exist!", username);
            errorMessage.append("Invalid session. ");
        } else {
            log.debug("SessionID for user {} OK", username);
        }

        if (userRepo.findByName(username) != null) {
            log.debug("Username {} invalid, could already exist!", username);
            errorMessage.append("Invalid username. ");
        } else {
            log.debug("Username {} OK", username);
        }

        if (user.getMoney() != 0) {
            log.debug("Money of user {} is invalid!", username);
            errorMessage.append("Invalid money. ");
        } else {
            log.debug("Money of user {} OK", username);
        }

        if (user.getRole().getName().equals(RoleService.ROLE_CHEATER)) {
            log.debug("Role of user {} is cheater! NOK", username);
        } else {
            log.debug("Role of user {} OK -> {}", username, user.getRole().getName());
        }

        if (errorMessage.isEmpty()) {
            log.debug("Validation of user {} OK", username);
            return UserService.VALIDATE_OK;
        } else {
            log.debug("Validation of user {} failed", username);
            log.debug("Sending error message: {}", errorMessage);
            return errorMessage.toString().trim();
        }
    }

    public String save(User user) {
        String userValidationResult = validateNewUser(user);
        if (userValidationResult.equals(UserService.VALIDATE_OK)) {
            try {
                userRepo.save(user);
            } catch (Exception e) {
                log.debug("Error saving user {}:{}", user.getId(), user.getName());
                return "Error saving user " + user.getId();
            }
            log.debug("Saved user {}:{}", user.getId(), user.getName());
            return UserService.VALIDATE_OK;
        } else {
            log.debug("Save user: error saving user: {}, error: {}", user, userValidationResult);
            return userValidationResult;
        }
    }
}
