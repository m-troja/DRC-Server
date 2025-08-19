package com.drc.server.controller;

import com.drc.server.dto.cnv.AnswerCnv;
import com.drc.server.entity.AnswerRequest;
import com.drc.server.service.AnswerService;
import com.drc.server.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

/*
 * Admin asks server to send specific answer of specific question to users of ROLE_USER
 * Cheater and Admin already knows all answers.
 */
@Slf4j
@Controller
public class AnswerWS {

    private final GameService gameService;
    private final AnswerCnv answerCnv;

    @MessageMapping("/answer")
    public void sendAnswerToUsers(AnswerRequest ar) {
        log.debug("Controller /answer was triggered with: {}", ar);

        sendAnswerToUsers(ar);
    }

    public AnswerWS(AnswerService answerService, GameService gameService, AnswerCnv answerCnv) {
        this.gameService = gameService;
        this.answerCnv = answerCnv;
    }
}
