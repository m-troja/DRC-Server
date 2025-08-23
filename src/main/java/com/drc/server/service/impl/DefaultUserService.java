package com.drc.server.service.impl;

import com.drc.server.entity.BalanceAction;
import com.drc.server.entity.Game;
import com.drc.server.entity.Role;
import com.drc.server.entity.User;
import com.drc.server.persistence.UserRepo;
import com.drc.server.service.GameService;
import com.drc.server.service.RoleService;
import com.drc.server.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void delete(User user) {
        try {
            log.debug("Trying to delete user {}", user);
            userRepo.delete(user);
            log.debug("User {} deleted", user);
        } catch (Exception e) {
            log.debug("Error deleting user {}: {}", user, e.toString());
        }
    }

    public User getUserByHttpSesssionid(String httpSessionid) {
        return userRepo.findByhttpSessionId(httpSessionid);
    }

    @Override
    public void update(User user) {
        userRepo.save(user);
    }

    public User getByStompSessionId(String stompSessionId) {
        return userRepo.findBystompSessionId(stompSessionId);
    }

    public User getUserByname(String name) {
        User user = userRepo.findByname(name);
        log.debug("Find user by name: {} : {}", name, user);
        return user;
    }

    public List<User> getUsers() {
        return userRepo.findAll();
    }

    public void setCheater(String username) {
        User user = getUserByname(username);
        user.setRole(roleService.getRoleByName(RoleService.ROLE_CHEATER));
        log.debug("Set cheater {} ", user);
    }

    public List<User> getUsersByRole(Role role) {
        List<User> users = userRepo.findByRole(role);
        log.debug("Found {} by {} ", users, role);
        return users;
    }

    public List<User> getUsersWithNoGame() {
        List<User> users = userRepo.findByGameIsNull();
        log.debug("Found users where game is null: {} ", users);
        return users;
    }

    public List<User> getUsersByRoleAndGame(Role role, Game game) {
        return userRepo.findByRoleAndGame(role, game);
    }

    public List<User> getUsersByGame(Game game) {
        List<User> users = userRepo.findByGame(game);
        log.debug("Find users by {} : {} ", game, users);
        return users;
    }

    public Double updateBalance(BalanceAction actionRequest, String username, String value) {

        Double valueDouble = Double.valueOf(value);

        User user = getUserByname(username);
        Double userMoney = user.getMoney();

        if (actionRequest.equals(BalanceAction.INCREASE)) {
            userMoney += valueDouble;
            log.debug("Money of {} was increased by {} ", user, valueDouble);
        } else if (actionRequest.equals(BalanceAction.DECREASE)) {
            userMoney -= valueDouble;
            log.debug("Money of {} was decreased by {} ", user, valueDouble);
        } else if (actionRequest.equals(BalanceAction.SET)) {
            userMoney = valueDouble;
            log.debug("Money of {} was set to {} ", user, valueDouble);
        } else if (actionRequest.equals(BalanceAction.DIVIDE)) {
            userMoney = userMoney / valueDouble;
            log.debug("Money of {} was divided by {} ", user, valueDouble);
        }
        user.setMoney(userMoney);
        update(user);
        return userMoney;
    }

    public void deleteAllUsers() {
        userRepo.deleteAll();
    }

    public User getUserById(Integer id) {
        return userRepo.findById(id).orElse(null);
    }
}