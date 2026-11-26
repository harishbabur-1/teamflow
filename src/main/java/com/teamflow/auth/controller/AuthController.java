
package com.teamflow.auth.controller;

import com.teamflow.auth.dto.LoginResponse;
import com.teamflow.auth.service.AuthService;
import com.teamflow.security.JwtService;
import com.teamflow.user.dto.LoginRequest;
import com.teamflow.user.dto.RegisterRequest;
import com.teamflow.user.dto.RegisterResponse;
import com.teamflow.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response = userService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(
            @Valid @RequestBody LoginRequest request) {

        var user = authService.authenticate(request);

        String token = jwtService.generateToken(user.getEmail());

        LoginResponse response = new LoginResponse(
                token,
                "Bearer"
        );

        return ResponseEntity.ok(response);
    }
}