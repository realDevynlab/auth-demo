package com.example.authdemo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(updatable = false, nullable = false)
	protected UUID id;

	@CreatedBy
	protected String createdBy;

	@CreatedDate
	protected LocalDateTime createdAt;

	@LastModifiedBy
	protected String updatedBy;

	@LastModifiedDate
	protected LocalDateTime updatedAt;

	protected Boolean deleted;

	protected LocalDateTime deletedAt;

	@Version
	protected Long version;

	protected BaseEntity() {}

}
