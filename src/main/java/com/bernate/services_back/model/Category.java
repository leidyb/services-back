package com.bernate.services_back.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Entity
@Table(name = "categories") // Nombre de tabla en BD
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria") // Nombre de columna para el ID de la categoría
    private Long id; // Nombre de variable Java

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Size(max = 45, message = "El nombre de la categoría no puede exceder los 45 caracteres")
    @Column(nullable = false, unique = true, length = 45) // El nombre de categoría debería ser único
    private String nombre;

    @NotNull(message = "El tipo de categoría es obligatorio")
    @Enumerated(EnumType.STRING) // Guardar el ENUM como String en la BD
    @Column(nullable = false, length = 20)
    private CategoryType tipo; // PRODUCTO o SERVICIO

    // Si quieres añadir una descripción opcional a la categoría
    // @Size(max = 255)
    // private String descripcion;

    // Relaciones inversas (opcionales, si quieres navegar desde Categoría a Productos/Servicios)
    // @OneToMany(mappedBy = "categoria")
    // private Set<Product> products;

    // @OneToMany(mappedBy = "categoria")
    // private Set<ServiceEntity> services;
}