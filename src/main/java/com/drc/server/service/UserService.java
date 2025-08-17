package com.drc.server.service;

import com.drc.server.entity.Game;
import com.drc.server.entity.Role;
import com.drc.server.entity.User;

import java.util.List;

public interface UserService {
    public final String VALIDATE_OK = "OK";

    String save(User user);
    void update(User user);
    String validateNewUser(User user);
    void delete(User user);
    User getUserByHttpSesssionid(String httpSessionid);
    User getByStompSessionId(String stompSesssionid);
    User getUserByname(String name);
    List<User> getUsers();
    void setCheater(String username);
    List<User> getUsersByRole(Role role);
    List<User> getUsersWithNoGame();
    List<User> getUsersByRoleAndGame(Role role, Game game);
}
