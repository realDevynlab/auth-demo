package com.example.authdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleService roleService;

    @Autowired
    public DataInitializer(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) {
        for (RoleName role : RoleName.values()) {
            if (!roleService.roleExists(role)) {
                roleService.createRole(role);
            }
        }
    }

}
