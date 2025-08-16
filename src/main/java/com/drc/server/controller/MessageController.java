package com.drc.server.controller;

import com.drc.server.entity.*;
import com.drc.server.service.QuestionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@AllArgsConstructor
@Controller
public class MessageController {

    private QuestionService questionService;

    @MessageMapping("/chat")
    @SendTo("/client/messages") //returns JSON
    public OutputMessage answerChat(Message message) {

        OutputMessage outputMessage ;

        if ( message.getText().equals("q")) {
            outputMessage = new OutputMessage(message.getFrom(), questionService.getQuestions().toString());
            return outputMessage;

        }

        outputMessage = new OutputMessage(message.getFrom(), message.getText());
        log.debug("Message to {} : {} " , message.getFrom(), message.getText() );

        return outputMessage;
    }

}
