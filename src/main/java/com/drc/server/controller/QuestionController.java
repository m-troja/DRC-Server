package com.drc.server.controller;

import com.drc.server.entity.Answer;
import com.drc.server.entity.Question;
import com.drc.server.entity.QuestionRequest;
import com.drc.server.service.AnswerService;
import com.drc.server.service.QuestionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
public class QuestionController {

    private AnswerService answerService;
    private QuestionService questionService;

    @MessageMapping("/question/")
    @SendToUser("/uestion") //returns JSON
    public Question sendQuestion(QuestionRequest qr) {

        Question question;
        List<Answer> answers;

        question = questionService.getQuestion(qr.id());

        if (qr.withAnswers()) {
            answers = answerService.getAnswersForQuestionId(question.getId());
            Question questionWithAnswers = new Question(question.getId(), question.getText(), answers);
            log.debug("Returning: {}" , questionWithAnswers);
            return questionWithAnswers;
        }
        else {
            Question questionWithoutAnswers = new Question(question.getId(), question.getText(), null);
            log.debug("Returning: {}" , questionWithoutAnswers);
            return questionWithoutAnswers;
        }
    }
}
