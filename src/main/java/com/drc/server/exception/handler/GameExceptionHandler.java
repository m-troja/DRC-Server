package com.drc.server.exception.handler;

import com.drc.server.entity.ErrorMessage;
import com.drc.server.entity.ErrorMessageType;
import com.drc.server.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class GameExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleGameNotFound(GameNotFoundException ex) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ErrorMessage em = new ErrorMessage(ErrorMessageType.GAME_NOT_FOUND, ex.getMessage(), now);
        log.debug("Exception handled: {}", em);
        return em;
    }

    @ExceptionHandler(GameCommandNotSupportedException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorMessage handleGameCommandNotSupported(GameCommandNotSupportedException ex) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ErrorMessage em = new ErrorMessage(ErrorMessageType.COMMAND_NOT_SUPPORTED, ex.getMessage(), now);
        log.debug("Exception handled: {}", em);
        return em;
    }

    @ExceptionHandler(GameErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleGameError(GameErrorException ex) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ErrorMessage em = new ErrorMessage(ErrorMessageType.GAME_ERROR, ex.getMessage(), now);
        log.debug("Exception handled: {}", em);
        return em;
    }

    @ExceptionHandler(GameMinimumPlayerException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ErrorMessage handleGameMinimumPlayer(GameMinimumPlayerException ex) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ErrorMessage em = new ErrorMessage(ErrorMessageType.MINIMUM_PLAYERS_NOT_REACHED, ex.getMessage(), now);
        log.debug("Exception handled: {}", em);
        return em;
    }

    @ExceptionHandler(NoNextQuestionException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNoNextQuestion(NoNextQuestionException ex) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ErrorMessage em = new ErrorMessage(ErrorMessageType.NO_NEXT_QUESTION, ex.getMessage(), now);
        log.debug("Exception handled: {}", em);
        return em;
    }
}
