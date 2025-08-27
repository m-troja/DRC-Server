package com.drc.server.dto.cnv;

import com.drc.server.dto.AnswerDto;
import com.drc.server.dto.CorrectAnswerResponseDto;
import com.drc.server.entity.Answer;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@NoArgsConstructor
public class AnswerCnv {

    public AnswerDto converAnswerToAnswerDto(Answer answer) {
        return new AnswerDto( answer.getValue(),answer.getText());
    }

    public List<AnswerDto> convertAnswersToAnswerDtos(List<Answer> answers) {
        List<AnswerDto> dtos = new ArrayList<>();

        for (Answer a : answers ) {
            dtos.add(converAnswerToAnswerDto(a));
        }
        return dtos;
    }

    public CorrectAnswerResponseDto convertAnswerToCorrectAnswerResponseDto(String username, Answer answer) {
        log.debug("Cnv input: username {}, answer {}", username, answer);
        CorrectAnswerResponseDto dto = new CorrectAnswerResponseDto(answer.getValue(),answer.getText(), username);
        log.debug("Cnv output: dto {}", dto);
        return dto;
    }
}
