package com.drc.server;

import com.drc.server.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

    @Autowired
    static RoleService roleService;

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}
}
