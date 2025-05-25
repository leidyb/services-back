package com.bernate.services_back.repository;

import com.bernate.services_back.model.Category;
import com.bernate.services_back.model.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {








    List<Category> findByTipo(CategoryType tipo);
    Optional<Category> findByNombre(String nombre);


    Optional<Category> findByNombreAndTipo(String nombre, CategoryType tipo);
}