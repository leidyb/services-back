package com.bernate.services_back.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class UpdateUserRolesRequest {

    @NotEmpty(message = "La lista de roles no puede estar vacía.")
    // Esperamos los nombres completos de los roles, ej. "ROLE_USER", "ROLE_ADMIN"
    private List<String> roles;
}