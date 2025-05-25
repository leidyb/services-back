package com.bernate.services_back.controller;

import com.bernate.services_back.dto.AuthResponse;
import com.bernate.services_back.dto.LoginRequest;
import com.bernate.services_back.dto.RegisterRequest;
import com.bernate.services_back.model.User;
import com.bernate.services_back.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap; // Asegúrate de tener estos imports
import java.util.Map;    // Asegúrate de tener estos imports

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") 
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = authService.registerUser(registerRequest);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuario '" + user.getUsername() + "' registrado exitosamente!");
            response.put("userId", user.getId().toString()); 

            return ResponseEntity.status(HttpStatus.CREATED).body(response); // <-- DEVOLVIENDO JSON

        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse); // <-- DEVOLVIENDO JSON PARA ERROR
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error: Usuario o contraseña incorrectos.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}