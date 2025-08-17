package com.drc.server.service.impl;

import com.drc.server.entity.Answer;
import com.drc.server.persistence.AnswerRepo;
import com.drc.server.service.AnswerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultAnswerService implements AnswerService {

    private final AnswerRepo answerRepo;

    public List<Answer> getAnswersForQuestionId(Integer questionId) {
        return answerRepo.findAllByQuestionId(questionId);
    }

    public DefaultAnswerService(AnswerRepo answerRepo) {
        this.answerRepo = answerRepo;
    }
}
