package com.bernate.services_back.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del servicio no puede estar vacío")
    @Size(min = 3, max = 150, message = "El nombre del servicio debe tener entre 3 y 150 caracteres")
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank(message = "La descripción del servicio no puede estar vacía")
    @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres")
    @Column(nullable = false, length = 1000)
    private String description;

    @PositiveOrZero(message = "El precio estimado debe ser positivo o cero")
    @Column(name = "estimated_price")
    private Double estimatedPrice;

    @Size(max = 255, message = "La URL de la imagen no puede exceder los 255 caracteres")
    @Column(length = 255, nullable = true)
    private String imagenes;

    @NotNull(message = "El estado de la oferta es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EstadoOferta estado;

    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id_categoria", nullable = false)
    private Category categoria;

    @NotNull(message = "El usuario que oferta el servicio es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offered_by_user_id", referencedColumnName = "id", nullable = false)
    private User ofertadoPor;


    public ServiceEntity(String name, String description, Double estimatedPrice, String imagenes, EstadoOferta estado, Category categoria, User ofertadoPor) {
        this.name = name;
        this.description = description;
        this.estimatedPrice = estimatedPrice;
        this.imagenes = imagenes;
        this.estado = estado;
        this.categoria = categoria;
        this.ofertadoPor = ofertadoPor;
    }
}