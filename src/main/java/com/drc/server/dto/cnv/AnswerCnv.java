package com.drc.server.dto.cnv;

import com.drc.server.dto.AnswerDto;
import com.drc.server.entity.Answer;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class AnswerCnv {

    public AnswerDto converAnswerToAnswerDto(Answer answer) {
        return new AnswerDto(answer.getId(), answer.getText());
    }
}
