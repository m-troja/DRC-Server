package com.drc.server.service.impl;

import com.drc.server.entity.Question;
import com.drc.server.persistence.QuestionRepo;
import com.drc.server.service.QuestionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DefaultQuestionService implements QuestionService {

    QuestionRepo questionRepo;

    @Override
    public void saveQuestion(Question question) {
        questionRepo.save(question);
    }

    @Override
    public List<Question> getQuestions() {
        return questionRepo.findAll();
    }

}
