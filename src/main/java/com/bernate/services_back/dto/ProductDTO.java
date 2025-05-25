package com.bernate.services_back.dto;

import com.bernate.services_back.model.EstadoOferta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser positivo o cero")
    private Double price;

    @PositiveOrZero(message = "El stock debe ser positivo o cero")
    private Integer stock;

    @Size(max = 255, message = "La URL de la imagen no puede exceder los 255 caracteres")
    private String imagenes;

    private EstadoOferta estado;





    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 45)
    private String categoryName;



    private String ofertadoPorUsername;
    private Long ofertadoPorId;



    public ProductDTO(Long id, String name, String description, Double price, Integer stock, String imagenes, EstadoOferta estado, String categoryName, String ofertadoPorUsername) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imagenes = imagenes;
        this.estado = estado;
        this.categoryName = categoryName;
        this.ofertadoPorUsername = ofertadoPorUsername;

    }








}