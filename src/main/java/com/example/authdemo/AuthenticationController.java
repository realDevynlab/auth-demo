package com.example.authdemo;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("login")
    public ResponseEntity<APIResponse<Map<String, AuthenticationResponse>>> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        AuthenticationResponse authenticationResponse = authenticationService.login(loginDTO);
        APIResponse<Map<String, AuthenticationResponse>> apiResponse = APIResponse.<Map<String, AuthenticationResponse>>builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Login successful")
                .data(Map.of("response", authenticationResponse))
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }

}
