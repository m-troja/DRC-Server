package com.drc.server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OutputMessage {

    private String from;
    private String text;
    private String date;
}
