package com.bernate.services_back.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;












@Entity
@Table(name = "products")
@Data
@NoArgsConstructor

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Column(length = 500)
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser positivo o cero")
    @Column(nullable = false)
    private Double price;

    @PositiveOrZero(message = "El stock debe ser positivo o cero")
    @Column(nullable = true)
    private Integer stock;


    @Size(max = 255, message = "La URL de la imagen no puede exceder los 255 caracteres")
    @Column(length = 255)
    private String imagenes;


    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = true)
    private EstadoOferta estado;


    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", referencedColumnName = "id_categoria", nullable = false)
    private Category categoria;


    @NotNull(message = "El usuario que oferta el producto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ofertado_por_id", referencedColumnName = "id", nullable = false)
    private User ofertadoPor;


    public Product(String name, String description, Double price, Integer stock, String imagenes, EstadoOferta estado, Category categoria, User ofertadoPor) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imagenes = imagenes;
        this.estado = estado;
        this.categoria = categoria;
        this.ofertadoPor = ofertadoPor;
    }



}