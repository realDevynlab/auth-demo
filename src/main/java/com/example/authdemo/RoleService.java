package com.example.authdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void createRole(RoleName roleName) {
        RoleEntity role = new RoleEntity();
        role.setName(roleName);
        roleRepository.save(role);
    }

    public boolean roleExists(RoleName roleName) {
        return roleRepository.findByName(roleName).isPresent();
    }

    public Optional<RoleEntity> findRoleByName(String roleName) {
        return roleRepository.findByName(RoleName.valueOf(roleName));
    }
}
