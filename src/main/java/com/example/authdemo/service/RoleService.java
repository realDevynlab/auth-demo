package com.example.authdemo.service;

import com.example.authdemo.entity.RoleEntity;
import com.example.authdemo.model.RoleName;
import com.example.authdemo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void createRole(RoleName role) {
        if (!roleExists(role)) {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(role);
            roleRepository.save(roleEntity);
        }
    }

    public boolean roleExists(RoleName roleName) {
        return roleRepository.findByName(roleName).isPresent();
    }

    public RoleEntity findByName(RoleName role) {
        return roleRepository.findByName(role).orElse(null);
    }

}
