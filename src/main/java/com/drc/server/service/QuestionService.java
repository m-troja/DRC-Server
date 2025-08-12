package com.drc.server.service;

import com.drc.server.entity.Question;

import java.util.List;

public interface QuestionService {

    void saveQuestion(Question question);

    List<Question> getQuestions();

    Question getQuestion(Integer id);

}
