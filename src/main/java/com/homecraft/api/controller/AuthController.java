package com.homecraft.api.controller;

import com.homecraft.api.dto.LoginDTO;
import com.homecraft.api.entity.AuthResult;
import com.homecraft.api.security.JwtUtil;
import com.homecraft.api.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {

        AuthResult result = authService.login(dto.getEmail(), dto.getPassword());

        if (!result.isSuccess()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", result.getErrorMessage()));
        }

        String token = jwtUtil.generateToken(
                result.getUserId(),
                result.getRole(),
                result.getEmail()
        );

        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "role", result.getRole()
                )
        );
    }
}

