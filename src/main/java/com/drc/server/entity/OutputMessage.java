package com.drc.server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Data
public class OutputMessage {

    private String from;
    private String text;
}
