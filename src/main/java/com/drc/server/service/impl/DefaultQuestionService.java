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
    public Question getQuestion(Integer id) {
        Question question;
        try {
            question = questionRepo.findById(id).orElse(null);
        }
        catch(Exception e) {
            return new Question(0, "Error:No question found!");
        }

        if (question == null) {
            return new Question(0, "Error:No question found!");
        }
        else {
            return question;
        }
    }
}
