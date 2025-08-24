package com.drc.server.controller;

import com.drc.server.dto.UserDto;
import com.drc.server.dto.cnv.UserCnv;
import com.drc.server.entity.Game;
import com.drc.server.entity.User;
import com.drc.server.service.GameService;
import com.drc.server.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping("/v1/")
public class UserRestController {

    private final UserService userService;
    private final GameService gameService;
    private UserCnv userCnv;

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam("gameId") Integer gameId) {
        if (gameId == 0) {
            return userCnv.convertUsersToUserDtos(userService.getUsersWithNoGame());
        }
        Game game = gameService.getGameById(gameId);
        List<User> users = userService.getUsersByGame(game);
        List<UserDto> userDtos = userCnv.convertUsersToUserDtos(users);
        log.debug("Get users by game: {}, {}", users, game);
        return userDtos;
    }

    @GetMapping("/user")
    public UserDto getUserByName(@RequestParam("name") String name) {
        User user = userService.getUserByname(name);
        UserDto userDto = userCnv.convertUserToUserDto(user);
        log.debug("Get users by name: {}, {}", user, name);
        return userDto;
    }
}
