package com.bernate.services_back.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor; // Para el constructor sin argumentos
import java.time.LocalDateTime;

@Data
@NoArgsConstructor // Necesario para la deserialización de Jackson y algunas operaciones JPA
public class RatingDTO {
    private Long id;

    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    private Integer score;

    @Size(max = 500, message = "El comentario no puede exceder los 500 caracteres")
    private String comment;

    private LocalDateTime createdAt;

    // Para la creación, el ID del rater se tomará del usuario autenticado.
    // No necesitamos raterUserId aquí en la petición si el servicio lo maneja.

    // Al menos uno de estos debe estar presente al crear una calificación
    private Long productId;
    private Long serviceId;

    // Campos para mostrar información en la respuesta
    private String raterUsername;
    private Long ratedProductId;    // ID del producto calificado (si aplica)
    private String ratedProductName; // Nombre del producto calificado (si aplica)
    private Long ratedServiceId;   // ID del servicio calificado (si aplica)
    private String ratedServiceName; // Nombre del servicio calificado (si aplica)
    private Long providerUserId; // ID del usuario que ofertó el producto/servicio

    // Constructor para cuando el servicio convierte la Entidad a DTO (para respuestas)
    public RatingDTO(Long id, Integer score, String comment, LocalDateTime createdAt,
                     String raterUsername, Long ratedProductId, String ratedProductName,
                     Long ratedServiceId, String ratedServiceName, Long providerUserId) {
        this.id = id;
        this.score = score;
        this.comment = comment;
        this.createdAt = createdAt;
        this.raterUsername = raterUsername;
        this.ratedProductId = ratedProductId;
        this.ratedProductName = ratedProductName;
        this.ratedServiceId = ratedServiceId;
        this.ratedServiceName = ratedServiceName;
        this.providerUserId = providerUserId;
    }
     // Constructor para la petición de creación desde el frontend (simplificado)
    public RatingDTO(Integer score, String comment, Long productId, Long serviceId) {
        this.score = score;
        this.comment = comment;
        this.productId = productId;
        this.serviceId = serviceId;
    }
    
  
}