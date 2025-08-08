package com.drc.server.persistence;

import com.drc.server.entity.Question;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepo extends CrudRepository<Question, Integer> {

    @Override
    List<Question> findAll();

}

