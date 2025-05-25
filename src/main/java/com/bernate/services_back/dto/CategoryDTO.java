package com.bernate.services_back.dto;

import com.bernate.services_back.model.CategoryType; // Importa tu Enum
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    // No incluimos el ID en el DTO para crear, pero sí lo enviaremos en la respuesta.
    // Por eso lo dejamos aquí, pero al crear se ignorará.
    private Long id;

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Size(max = 45)
    private String nombre;

    @NotNull(message = "El tipo de categoría es obligatorio (PRODUCTO o SERVICIO)")
    private CategoryType tipo;

    // Si añadiste 'descripcion' a tu entidad Category, también deberías añadirla aquí.
    // private String descripcion;
}