package com.drc.server.dto.cnv;

import com.drc.server.dto.AnswerDto;
import com.drc.server.entity.Answer;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
public class AnswerCnv {

    public AnswerDto converAnswerToAnswerDto(Answer answer) {
        return new AnswerDto(answer.getId(), answer.getText());
    }
    public List<AnswerDto> converAnswersToAnswerDtos(List<Answer> answers) {
        List<AnswerDto> dtos = new ArrayList<>();

        for (Answer a : answers ) {
            dtos.add(converAnswerToAnswerDto(a));
        }
        return dtos;

    }
}
