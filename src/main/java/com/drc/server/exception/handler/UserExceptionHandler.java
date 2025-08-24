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
public class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleUserNotFound(UserNotFoundException ex) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ErrorMessage em = new ErrorMessage(ErrorMessageType.USER_NOT_FOUND, ex.getMessage(), now);
        log.debug("Exception handled: {}", em);
        return em;
    }

    @ExceptionHandler(SetCheaterException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleSetCheater(SetCheaterException ex) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ErrorMessage em = new ErrorMessage(ErrorMessageType.SET_CHEATER_ERROR, ex.getMessage(), now);
        log.debug("Exception handled: {}", em);
        return em;
    }
}
