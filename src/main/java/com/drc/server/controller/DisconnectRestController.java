package com.drc.server.controller;

import com.drc.server.entity.Game;
import com.drc.server.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/disconnect")
@RestController
@Slf4j
public class DisconnectRestController {

    @GetMapping("/cmd")
    public String startGame(String respose) {
        return "OK";
    }


}
