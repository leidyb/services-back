package com.bernate.services_back.dto;

import com.bernate.services_back.model.EstadoOferta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceDTO {

    private Long id;

    @NotBlank(message = "El nombre del servicio no puede estar vacío")
    @Size(min = 3, max = 150)
    private String name;

    @NotBlank(message = "La descripción del servicio no puede estar vacía")
    @Size(max = 1000)
    private String description;

    @PositiveOrZero(message = "El precio estimado debe ser positivo o cero")
    private Double estimatedPrice; // Puede ser null

    @Size(max = 255)
    private String imagenes; // URL o nombre de archivo (según tu lógica en el servicio)

    @NotNull(message = "El estado de la oferta es obligatorio")
    private EstadoOferta estado;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 45)
    private String categoryName; // Nombre de la categoría (tipo SERVICIO)

    private String ofertadoPorUsername; // Para respuestas
    // No se suele enviar el ID del ofertante al crear/actualizar, se toma del contexto de seguridad

    // Constructor explícito para asegurar el orden al mapear desde la entidad
    public ServiceDTO(Long id, String name, String description, Double estimatedPrice, String imagenes, EstadoOferta estado, String categoryName, String ofertadoPorUsername) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.estimatedPrice = estimatedPrice;
        this.imagenes = imagenes;
        this.estado = estado;
        this.categoryName = categoryName;
        this.ofertadoPorUsername = ofertadoPorUsername;
    }
}