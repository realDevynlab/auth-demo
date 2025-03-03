package com.example.authdemo.repository;

import com.example.authdemo.entity.RoleEntity;
import com.example.authdemo.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findByName(RoleName name);

}
