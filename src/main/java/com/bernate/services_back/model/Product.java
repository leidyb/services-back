package com.bernate.services_back.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
// Quita @AllArgsConstructor si no lo necesitas explícitamente o si defines tus propios constructores principales.
// Si lo dejas, asegúrate de que cualquier código que lo use esté actualizado con todos los campos.
// import lombok.AllArgsConstructor; 

// Asegúrate de que este Enum exista en com.bernate.services_back.model o com.bernate.services_back.model.enums
// enum EstadoOferta {
//     ACTIVO,
//     INACTIVO,
//     PENDIENTE,
//     VENDIDO
// }

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
// @AllArgsConstructor // Considera si realmente necesitas este con todos los campos.
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Nombre de campo Java: id -> Columna BD: id (por defecto)

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String name; // Nombre de campo Java: name -> Columna BD: name (por defecto)

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Column(length = 500)
    private String description; // Nombre de campo Java: description -> Columna BD: description

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser positivo o cero")
    @Column(nullable = false)
    private Double price; // Nombre de campo Java: price -> Columna BD: price

    @PositiveOrZero(message = "El stock debe ser positivo o cero")
    @Column(nullable = true) // Asumiendo que el stock puede ser nulo o lo manejas como 0
    private Integer stock; // Nombre de campo Java: stock -> Columna BD: stock

    // --- CAMPO IMAGENES AÑADIDO ---
    @Size(max = 255, message = "La URL de la imagen no puede exceder los 255 caracteres")
    @Column(length = 255)
    private String imagenes; // Nombre de campo Java: imagenes -> Columna BD: imagenes

    // --- CAMPO ESTADO AÑADIDO ---
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = true) // Opcional, o ponle un valor por defecto si es NOT NULL
    private EstadoOferta estado; // Nombre de campo Java: estado -> Columna BD: estado

    // --- RELACIÓN CON CATEGORY (YA LA TENÍAS) ---
    @NotNull(message = "La categoría es obligatoria") // Hacemos la categoría obligatoria
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", referencedColumnName = "id_categoria", nullable = false) // Simplifiqué el nombre de la FK
    private Category categoria; // Nombre de campo Java: categoria -> Columna BD: categoria_id

    // --- RELACIÓN CON USER (OFERTADO POR) AÑADIDA ---
    @NotNull(message = "El usuario que oferta el producto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ofertado_por_id", referencedColumnName = "id", nullable = false) // Asumiendo que el ID en User es 'id'
    private User ofertadoPor; // Nombre de campo Java: ofertadoPor -> Columna BD: ofertado_por_id

    // Podrías definir constructores específicos si los necesitas, por ejemplo:
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

    // Si dejaste @AllArgsConstructor, este constructor no sería estrictamente necesario,
    // pero tener constructores explícitos a veces es más claro.
}