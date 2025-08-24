package com.drc.server.entity;

public record ErrorMessage(ErrorMessageType errorType, String message, String timestamp)
{
}
