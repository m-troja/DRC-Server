package com.drc.server.service.impl;

import com.drc.server.entity.*;
import com.drc.server.exception.UserNotFoundException;
import com.drc.server.persistence.UserRepo;
import com.drc.server.service.RoleService;
import com.drc.server.service.UserService;
import com.drc.server.service.notification.UserNotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Slf4j
@Service
public class DefaultUserService implements UserService {

    private final RoleService roleService;
    private final UserRepo userRepo;
    private final UserNotificationService userNotificationService;

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
        User user = null;
        try {
            user = userRepo.findByname(name);
        } catch (Exception e) {
            throw new UserNotFoundException("User " + name + " not found");
        }
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

    public void deleteAllUsers() {
        try {
            userRepo.deleteAll();
            log.debug("Removed all users");
        } catch (Exception e) {
            log.debug("Error removing all users");
            throw new RuntimeException(e);
        }
    }

    public User getUserById(Integer id) {
        return userRepo.findById(id).orElseThrow( () -> new UserNotFoundException("GameId " + id + " was not found"));
    }

    public void kick(String username) {
        KickRequest kr = new KickRequest(RequestType.COMMAND_DISCONNECT, username);
        userNotificationService.sendKickRequest(kr);

    }
}