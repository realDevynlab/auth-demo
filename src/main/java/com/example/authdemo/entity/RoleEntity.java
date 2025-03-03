package com.example.authdemo.entity;

import com.example.authdemo.model.RoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "roles")
public class RoleEntity extends BaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(name = "name", nullable = false, unique = true)
	private RoleName name;

	@ManyToMany(mappedBy = "roles")
	private Set<UserEntity> users = new HashSet<>();

}
