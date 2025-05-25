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
    private Double estimatedPrice;

    @Size(max = 255)
    private String imagenes;

    @NotNull(message = "El estado de la oferta es obligatorio")
    private EstadoOferta estado;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 45)
    private String categoryName;

    private String ofertadoPorUsername;



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