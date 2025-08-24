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
public class BalanceExceptionHandler {

    @ExceptionHandler(BalanceActionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleUserNotFound(BalanceActionException ex) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ErrorMessage em = new ErrorMessage(ErrorMessageType.BALANCE_ACTION_NOT_ALLOWED, ex.getMessage(), now);
        log.debug("Exception handled: {}", em);
        return em;
    }

    @ExceptionHandler(BalanceUsernameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleUserNotFound(BalanceUsernameException ex) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ErrorMessage em = new ErrorMessage(ErrorMessageType.BALANCE_WRONG_USERNAME, ex.getMessage(), now);
        log.debug("Exception handled: {}", em);
        return em;
    }
    @ExceptionHandler(BalanceValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleUserNotFound(BalanceValueException ex) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ErrorMessage em = new ErrorMessage(ErrorMessageType.BALANCE_WRONG_VALUE, ex.getMessage(), now);
        log.debug("Exception handled: {}", em);
        return em;
    }
}
