package com.drc.server.dto.cnv;

import com.drc.server.dto.QuestionDto;
import com.drc.server.entity.Question;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class QuestionCnv {

    public QuestionDto convertQuestionToQuestionDto(Question question) {
        return new QuestionDto(question.getId(), question.getText());
    }
}
