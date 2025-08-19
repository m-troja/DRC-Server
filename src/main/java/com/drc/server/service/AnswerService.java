package com.drc.server.service;

import com.drc.server.entity.Answer;

import java.util.List;

public interface AnswerService {

    List<Answer> getAnswersForQuestionId(Integer questionId);
    Answer getAnswerForQuestionByValueAndGameId(Double value, Integer questionId);
    Answer getAnswerById(Integer id);
    Answer getAnswerByValue(Double value);
}
