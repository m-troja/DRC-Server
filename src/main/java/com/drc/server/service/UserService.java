package com.drc.server.service;

import com.drc.server.entity.User;

public interface UserService {

    boolean save(User user);
    boolean validateNewUser(User user);
    void delete(User user);
    User getUserBySesssionid(String sessionid);
}
