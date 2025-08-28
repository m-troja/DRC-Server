package com.drc.server.entity;

import com.drc.server.dto.UserDto;

public record ShootPlayerNotification(ResponseType responseType, UserDto user) {
}
