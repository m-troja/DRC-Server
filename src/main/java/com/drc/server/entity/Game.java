package com.drc.server.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Data
@Component
public class Game {

    private final List<User> users;

}
