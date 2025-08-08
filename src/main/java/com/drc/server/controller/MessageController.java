package com.drc.server.controller;

import com.drc.server.entity.Message;
import com.drc.server.entity.OutputMessage;
import com.drc.server.service.QuestionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Controller
public class MessageController {

    QuestionService questionService;

    @MessageMapping("/chat")
    @SendTo("/topic/messages") //returns JSON
    public OutputMessage send(Message message) {

        if ( message.getText().equals("q")) {
            log.debug("Message to {} : {} " , message.getFrom() , questionService.getQuestions().toString() );
            return new OutputMessage(message.getFrom(), questionService.getQuestions().toString());
        }

        log.debug("Message from {} : {} " , message.getFrom(), message.getText() );

        return new OutputMessage(message.getFrom(), message.getText());
    }
}
