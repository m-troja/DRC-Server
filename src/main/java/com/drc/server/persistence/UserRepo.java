package com.drc.server.persistence;

import com.drc.server.entity.Game;
import com.drc.server.entity.Role;
import com.drc.server.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends CrudRepository<User, Integer> {

    public User findByName(String name);
    public User findByhttpSessionId(String httpSessionId);
    public User findBystompSessionId(String stompSessionId);
    public User findByname(String name);
    public List<User> findAll();
    public List<User> findByRole(Role role);
    public List<User> findByGameIsNull();
    public List<User> findByRoleAndGame(Role role, Game game);
    public List<User> findByGame(Game game);

}

