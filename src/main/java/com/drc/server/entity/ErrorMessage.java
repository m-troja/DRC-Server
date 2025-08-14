package com.drc.server.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorMessage {

    private final String type;
    private final String message;
    private final String sessionid;
    private final String username;
    private final String timestamp;

}
