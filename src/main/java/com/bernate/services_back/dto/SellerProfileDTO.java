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
    private String nombre;    // Nombre propio del vendedor
    private String apellido;  // Apellido del vendedor
    private String ubicacion; // Ubicación del vendedor (si quieres mostrarla)
    // Puedes añadir más campos públicos del User si lo deseas

    private Double averageProductRating;
    private Long totalProductRatings;
    private Double averageServiceRating;
    private Long totalServiceRatings;
    private Double overallAverageRating; // Un promedio general
    private Long totalOverallRatings;    // Total de calificaciones recibidas
}