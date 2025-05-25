package com.bernate.services_back.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank(message = "El nombre propio no puede estar vacío")
    @Size(max = 45)
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(max = 45)
    private String apellido;

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "Debe ser una dirección de correo electrónico válida")
    @Size(max = 45)
    private String correo;

    // --- NUEVOS CAMPOS OPCIONALES ---
    @Size(max = 45, message = "El teléfono no puede exceder los 45 caracteres")
    private String telefono; // Opcional, no @NotBlank

    @Size(max = 100, message = "La ubicación no puede exceder los 100 caracteres")
    private String ubicacion; // Opcional, no @NotBlank
}