package com.drc.server.persistence;

import com.drc.server.entity.Answer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepo extends CrudRepository<Answer, Integer> {

    @Query("SELECT a FROM Answer a WHERE a.question.id = ?1")
    public List<Answer> findAllByQuestionId(Integer questionId);
}
