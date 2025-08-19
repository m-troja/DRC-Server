package com.drc.server.service.impl;

import com.drc.server.entity.Answer;
import com.drc.server.persistence.AnswerRepo;
import com.drc.server.service.AnswerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DefaultAnswerService implements AnswerService {

    private final AnswerRepo answerRepo;

    public List<Answer> getAnswersForQuestionId(Integer questionId) {
        return answerRepo.findAllByQuestionId(questionId);
    }

    public Answer getAnswerForQuestionByValueAndGameId(Double value, Integer questionId) {
        Answer answer = answerRepo.findByValueAndQuestionId(value, questionId);
        log.debug("GetAnswerForQuestion: answer.value{}, answer.questionId {}, answer: {}", value, questionId, answer);
        return answer;
    }

    public Answer getAnswerById(Integer id) {
        Answer answer = answerRepo.findById(id).orElse(null);
        log.debug("Get answer by id {}: {}", id, answer);
        return answer;
    }

    public Answer getAnswerByValue(Double value) {
        Answer answer = answerRepo.findByValue(value);
        log.debug("Get answer by value {}: {}", value, answer);
        return answer;
    }

    public DefaultAnswerService(AnswerRepo answerRepo) {
        this.answerRepo = answerRepo;
    }
}
