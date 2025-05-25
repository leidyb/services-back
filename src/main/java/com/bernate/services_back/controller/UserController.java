package com.bernate.services_back.controller;

import com.bernate.services_back.dto.SellerProfileDTO;
import com.bernate.services_back.service.AuthService; // O el servicio donde pusiste getSellerProfileByUsername
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users") // Ruta base para perfiles de usuario públicos
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final AuthService authService; // O tu UserService/UserProfileService

    @Autowired
    public UserController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Obtiene el perfil público de un vendedor, incluyendo su calificación promedio.
     * @param username El username del vendedor.
     * @return SellerProfileDTO con los datos.
     */
    @GetMapping("/{username}/profile")
    public ResponseEntity<SellerProfileDTO> getSellerProfile(@PathVariable String username) {
        SellerProfileDTO profile = authService.getSellerProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }
}