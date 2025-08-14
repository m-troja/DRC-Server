package com.drc.server.entity;

public record ErrorMessage(String type, String message,
                           String sessionid, String username,
                           String timestamp)
{
}
