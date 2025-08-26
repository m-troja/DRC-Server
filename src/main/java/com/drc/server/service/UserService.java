package com.drc.server.service;

import com.drc.server.entity.BalanceAction;
import com.drc.server.entity.Game;
import com.drc.server.entity.Role;
import com.drc.server.entity.User;

import java.util.List;

public interface UserService {
    public final String VALIDATE_OK = "OK";

    void update(User user);
    void delete(User user);
    User getUserByHttpSesssionid(String httpSessionid);
    User getByStompSessionId(String stompSesssionid);
    User getUserByname(String name);
    List<User> getUsers();
    List<User> getUsersByRole(Role role);
    List<User> getUsersWithNoGame();
    List<User> getUsersByRoleAndGame(Role role, Game game);
    List<User> getUsersByGame(Game game);
    Double updateBalance(BalanceAction action, String username, String value );
    void deleteAllUsers();
    User getUserById(Integer id);
    void kick(String username);
}
