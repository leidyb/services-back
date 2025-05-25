package com.bernate.services_back.dto;

import com.bernate.services_back.model.EstadoOferta; // Importa tu Enum
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

    private Long id; // Se enviará en respuestas, opcional en peticiones de creación

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
    private String imagenes; // Campo para la URL de la imagen

    private EstadoOferta estado; // Usamos el Enum directamente para el tipo de estado

    // Para la categoría, al crear/actualizar un producto, podríamos enviar solo el ID o el NOMBRE de la categoría.
    // Al mostrar un producto, podríamos querer mostrar el nombre.
    // Por simplicidad en el DTO de entrada y salida, usaremos el nombre de la categoría.
    // El servicio se encargará de buscar la entidad Category por su nombre.
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 45)
    private String categoryName; // Nombre de la categoría

    // Para el usuario que oferta, al crear podríamos tomarlo del usuario autenticado.
    // Al mostrar, podríamos querer el username.
    private String ofertadoPorUsername; // Username del usuario que oferta (para respuestas)
    private Long ofertadoPorId;         // ID del usuario que oferta (podría usarse en peticiones si un admin crea por otro)


    // Constructor para facilitar la creación desde la Entidad en el Servicio (para respuestas)
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
        // this.ofertadoPorId no se incluye en este constructor de salida, pero podría ser útil en DTOs de entrada
    }

    // Podrías tener otros constructores o usar @AllArgsConstructor y tener cuidado.
    // Si usas @AllArgsConstructor, asegúrate de que el orden de los campos sea el que esperas
    // o usa constructores específicos como el de arriba.
    // Por ahora, para mantener el ejemplo de la pregunta original, voy a comentar @AllArgsConstructor
    // y dejaré el constructor que definimos y el @NoArgsConstructor.
    // Si descomentas @AllArgsConstructor, este constructor específico de arriba podría ser redundante
    // a menos que el orden de @AllArgsConstructor no te sirva.
}