package com.example.authdemo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("signup")
    public ResponseEntity<APIResponse<Map<String, UserDTO>>> signup(@Valid @RequestBody SignupRequest signupRequest, HttpServletRequest request) {
        UserDTO userDTO = userService.signup(signupRequest);
        APIResponse<Map<String, UserDTO>> apiResponse = APIResponse.<Map<String, UserDTO>>builder()
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .message("Signup successful")
                .data(Map.of("user", userDTO))
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("test")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public String test() {
        return "test";
    }

}
