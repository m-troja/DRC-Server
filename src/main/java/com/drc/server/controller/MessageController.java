package com.drc.server.controller;

import com.drc.server.entity.*;
import com.drc.server.service.AnswerService;
import com.drc.server.service.QuestionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Controller
public class MessageController {

    private QuestionService questionService;
    private AnswerService answerService;

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

    @MessageMapping("/question/")
    @SendTo("/client/question") //returns JSON
    public Question sendQuestion(QuestionRequest qr) {
        Question question;
        List<Answer> answers;
        question = questionService.getQuestion(qr.id());

        if (qr.withAnswers()) {
            answers = answerService.getAnswersForQuestion(question.getId());
            question.setAnswers(answers);
            return new Question(question.getId(), question.getText(), answers);
        }
        else {
            return new Question(question.getId(), question.getText(), null);
        }
    }
}
