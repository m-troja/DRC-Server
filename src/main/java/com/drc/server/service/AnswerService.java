package com.drc.server.service;

import com.drc.server.entity.Answer;

import java.util.List;

public interface AnswerService {

    List<Answer> getAnswersForQuestion(Integer questionId);
}
