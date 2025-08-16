package com.drc.server.persistence;

import com.drc.server.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends CrudRepository<User, Integer> {

    public User findByName(String name);
    public User findByhttpSessionId(String httpSessionId);
    public User findBystompSessionId(String stompSessionId);
}

