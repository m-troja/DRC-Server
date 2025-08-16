package com.drc.server.entity;

public record ErrorMessage(ErrorMessageType type, String message, String timestamp)
{
}
