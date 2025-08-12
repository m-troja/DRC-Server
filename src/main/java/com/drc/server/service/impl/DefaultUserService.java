package com.drc.server.service.impl;

import com.drc.server.entity.User;
import com.drc.server.persistence.UserRepo;
import com.drc.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DefaultUserService implements UserService {

    UserRepo userRepo;

    public boolean save(User user) {
        if (userRepo.findByName(user.getName()) != null) {
            userRepo.save(user);
            log.debug("User {}:{} saved!", user.getId(), user.getName());
            return true;
        }
        log.debug("Error saving user {}:{} - already exists!", user.getId(), user.getName());
        return false;

    }

    public DefaultUserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
}
