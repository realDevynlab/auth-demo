package com.example.authdemo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T> {
	protected HttpStatus status;
	protected Integer statusCode;
	protected String message;
	protected T data;
	protected String path;
	protected LocalDateTime timestamp;
}
