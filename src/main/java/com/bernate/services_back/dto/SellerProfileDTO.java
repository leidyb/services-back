package com.bernate.services_back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerProfileDTO {
    private Long userId;
    private String username;
    private String nombre;
    private String apellido;
    private String ubicacion;


    private Double averageProductRating;
    private Long totalProductRatings;
    private Double averageServiceRating;
    private Long totalServiceRatings;
    private Double overallAverageRating;
    private Long totalOverallRatings;
}