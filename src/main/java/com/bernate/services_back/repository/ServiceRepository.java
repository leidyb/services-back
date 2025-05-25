package com.bernate.services_back.repository;

import com.bernate.services_back.model.Category;
import com.bernate.services_back.model.ServiceEntity;
import com.bernate.services_back.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query; // Si necesitas consultas JPQL complejas
// import org.springframework.data.repository.query.Param; // Si usas @Query con parámetros
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    // Método para encontrar servicios por categoría con paginación
    Page<ServiceEntity> findByCategoria(Category categoria, Pageable pageable);

    // Método para encontrar servicios por el usuario que los oferta con paginación
    Page<ServiceEntity> findByOfertadoPor(User ofertadoPor, Pageable pageable);

    // Método para búsqueda combinada (nombre, descripción, nombre de categoría) con paginación
    // Spring Data JPA generará la consulta para buscar en ServiceEntity.name, ServiceEntity.description,
    // y navegará a ServiceEntity.categoria.name (asumiendo que Category tiene un campo 'name')
    Page<ServiceEntity> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoriaNombreContainingIgnoreCase(
        String nameTerm,
        String descriptionTerm,
        String categoriaNombreTerm, // Corresponde a serviceEntity.categoria.nombre
        Pageable pageable
    );

    // Alternativa si 'categoria' en ServiceEntity fuera un String y no un objeto Category:
    // Page<ServiceEntity> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(
    //     String nameTerm,
    //     String descriptionTerm,
    //     String categoryTerm, // Para buscar en un campo 'category' de tipo String en ServiceEntity
    //     Pageable pageable
    // );
}