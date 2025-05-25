package com.bernate.services_back.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class UpdateUserRolesRequest {

    @NotEmpty(message = "La lista de roles no puede estar vacía.")

    private List<String> roles;
}