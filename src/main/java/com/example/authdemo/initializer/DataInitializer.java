package com.example.authdemo.initializer;

import com.example.authdemo.entity.UserEntity;
import com.example.authdemo.model.RoleName;
import com.example.authdemo.service.RoleService;
import com.example.authdemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    private final RoleService roleService;
    private final UserService userService;

    @Override
    @Transactional
    public void run(String... args) {
        createRoles();
        createSuperuser();
    }

    private void createRoles() {
        for (RoleName role : RoleName.values()) {
            if (!roleService.roleExists(role)) {
                roleService.createRole(role);
            }
        }
    }

    private void createSuperuser() {
        if (!userService.userExists(adminUsername)) {
            UserEntity superuser = new UserEntity();
            superuser.setUsername(adminUsername);
            superuser.setEmail(adminEmail);
            superuser.setPassword(adminPassword);
            for (RoleName role : RoleName.values()) {
                superuser.addRole(roleService.findByName(role));
            }
            userService.createUser(superuser);
        }
    }

}
