package com.drc.server.entity;

import com.drc.server.dto.UserDto;

public record NotificationToAdminAboutUser(ResponseType messageType, UserDto user) {
}
