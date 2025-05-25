package com.bernate.services_back.controller;

import com.bernate.services_back.dto.UpdateUserRolesRequest;
import com.bernate.services_back.dto.UserResponseDTO; // Asegúrate de importar este DTO
import com.bernate.services_back.exception.ResourceNotFoundException; // Importa si tu servicio la lanza
import com.bernate.services_back.model.User;
import com.bernate.services_back.service.AuthService; // O UserService si la lógica está allí
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Para proteger los métodos
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List; // Importar List
import java.util.Map;

@RestController
@RequestMapping("/api/admin") // Ruta base para todos los endpoints de este controlador
@CrossOrigin(origins = "http://localhost:5173") // O tu configuración global de CORS
public class AdminController {

    @Autowired
    private AuthService authService; // Asumiendo que la lógica de usuario está en AuthService

    /**
     * Endpoint para actualizar los roles de un usuario específico.
     * Solo accesible por usuarios con ROLE_ADMIN.
     *
     * @param username El nombre del usuario cuyos roles se actualizarán.
     * @param updateUserRolesRequest DTO que contiene la lista de nuevos roles.
     * @return ResponseEntity con un mensaje de éxito o error.
     */
    @PostMapping("/users/{username}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignRolesToUser(
            @PathVariable String username,
            @Valid @RequestBody UpdateUserRolesRequest updateUserRolesRequest) {
        try {
            User updatedUser = authService.updateUserRoles(username, updateUserRolesRequest);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Roles actualizados para el usuario: " + updatedUser.getUsername());
            response.put("newRoles", updatedUser.getRoles());
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Endpoint para obtener una lista de todos los usuarios.
     * Solo accesible por usuarios con ROLE_ADMIN.
     *
     * @return ResponseEntity con la lista de UserResponseDTO o un error.
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = authService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}