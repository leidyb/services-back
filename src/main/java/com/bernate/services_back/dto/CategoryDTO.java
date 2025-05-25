package com.bernate.services_back.dto;

import com.bernate.services_back.model.CategoryType;
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



    private Long id;

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Size(max = 45)
    private String nombre;

    @NotNull(message = "El tipo de categoría es obligatorio (PRODUCTO o SERVICIO)")
    private CategoryType tipo;



}