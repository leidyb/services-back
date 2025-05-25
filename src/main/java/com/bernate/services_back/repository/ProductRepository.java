package com.bernate.services_back.repository;

import com.bernate.services_back.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByNameIgnoreCase(String name); // Ejemplo, si lo tienes

    // MÉTODO DE BÚSQUEDA CORREGIDO:
    // Cambiamos "Category" a "Categoria" para que coincida con el nombre del campo en la entidad Product.java
    // También, como 'categoria' es un objeto de tipo Category, para buscar por su nombre,
    // necesitamos indicarle que navegue hasta la propiedad 'nombre' de ese objeto 'categoria'.
    // Esto se hace con 'CategoriaNombre'.
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoriaNombreContainingIgnoreCase(
        String nameTerm, 
        String descriptionTerm, 
        String categoriaNombreTerm, // Cambiamos el nombre del parámetro para claridad
        Pageable pageable
    );

    // Si tuvieras otros métodos como findByCategory(String categoryName), deberías renombrarlo a
    // findByCategoriaNombre(String categoriaNombre) o similar si 'categoria' es el objeto.
    // O si 'categoria' en la entidad Product fuera un simple String y no un objeto Category,
    // entonces sí usarías findByCategoriaContainingIgnoreCase. Pero como es un objeto,
    // necesitamos acceder a la propiedad 'nombre' de ese objeto 'categoria'.
}