package com.bernate.services_back.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RatingDTO {
    private Long id;

    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    private Integer score;

    @Size(max = 500, message = "El comentario no puede exceder los 500 caracteres")
    private String comment;

    private LocalDateTime createdAt;





    private Long productId;
    private Long serviceId;


    private String raterUsername;
    private Long ratedProductId;
    private String ratedProductName;
    private Long ratedServiceId;
    private String ratedServiceName;
    private Long providerUserId;


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

    public RatingDTO(Integer score, String comment, Long productId, Long serviceId) {
        this.score = score;
        this.comment = comment;
        this.productId = productId;
        this.serviceId = serviceId;
    }
    
  
}