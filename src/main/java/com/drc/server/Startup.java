package com.drc.server;

import com.drc.server.service.RoleService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Startup {

    RoleService roleService;

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        roleService.createRoles();
    }

    public Startup(RoleService roleService) {
        this.roleService = roleService;
    }
}
