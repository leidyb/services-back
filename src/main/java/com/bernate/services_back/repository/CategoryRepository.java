package com.bernate.services_back.repository;

import com.bernate.services_back.model.Category;
import com.bernate.services_back.model.CategoryType; // Importa tu Enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // JpaRepository ya nos provee:
    // - save(Category category)
    // - findById(Long id)
    // - findAll()
    // - deleteById(Long id)
    // ... y más.

    List<Category> findByTipo(CategoryType tipo);
    Optional<Category> findByNombre(String nombre); // Ya deberías tener este

    // --- NUEVO MÉTODO ---
    Optional<Category> findByNombreAndTipo(String nombre, CategoryType tipo);
}