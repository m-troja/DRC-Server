package com.drc.server.service;

import com.drc.server.entity.User;

public interface UserService {
    public final String VALIDATE_OK = "OK";

    String save(User user);
    String validateNewUser(User user);
    void delete(User user);
    User getUserBySesssionid(String sessionid);
}
