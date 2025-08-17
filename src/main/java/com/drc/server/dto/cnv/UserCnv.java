package com.drc.server.dto.cnv;

import com.drc.server.dto.UserDto;
import com.drc.server.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserCnv {

    public UserDto convertUserToUserDto(User user) {
        Integer id = 0;
        if ( user.getId() != null) {
            id = user.getId();
        }

        String name = "";
        if ( user.getName() != null ) {
            name = user.getName();
        }

        Double money = 0.0;
        if ( user.getMoney() != null ) {
            money =  user.getMoney();
        }
        String role = "";
        if ( user.getRole().getName() != null ) {
            role =  user.getRole().getName();
        }

        Integer gameId = -1;
        if ( user.getGame() != null) {
            if ( user.getGame().getId() != null) {
                gameId = user.getGame().getId();
            }
        }

        UserDto userDto = new UserDto(id, name, money, role, gameId);
        log.debug("User: {}", user);
        log.debug("UserDto: {}", userDto);
        return userDto;
    }

    public List<UserDto> convertUsersToUserDtos(List<User> users) {
        List<UserDto> userDtos = new ArrayList<>();

        for (User user : users) {
            userDtos.add(convertUserToUserDto(user));
        }
        log.debug("UserDtos: {}", userDtos);
        return userDtos;
    }
}
