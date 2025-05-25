package com.bernate.services_back.service;

import com.bernate.services_back.dto.CategoryDTO;
import com.bernate.services_back.model.Category;
import com.bernate.services_back.model.CategoryType;
import com.bernate.services_back.repository.CategoryRepository;
import com.bernate.services_back.exception.ResourceNotFoundException; // Asumiendo que tienes esta excepción
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // --- Método para convertir Entidad a DTO ---
    private CategoryDTO convertToDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getNombre(),
                category.getTipo()
                // category.getDescripcion() // Si lo añadiste a la entidad y DTO
        );
    }

    // --- Método para convertir DTO a Entidad (para creación/actualización) ---
    private Category convertToEntity(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setNombre(categoryDTO.getNombre());
        category.setTipo(categoryDTO.getTipo());
        // category.setDescripcion(categoryDTO.getDescripcion()); // Si lo añadiste
        return category;
    }

    // --- Operaciones CRUD y otras ---

    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // Aquí podrías añadir validaciones, como verificar si ya existe una categoría con el mismo nombre y tipo.
        // Por ejemplo:
        // if (categoryRepository.findByNombreAndTipo(categoryDTO.getNombre(), categoryDTO.getTipo()).isPresent()) {
        //     throw new IllegalArgumentException("Ya existe una categoría con ese nombre y tipo.");
        // }
        Category category = convertToEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        return convertToDTO(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getCategoriesByType(CategoryType tipo) {
        return categoryRepository.findByTipo(tipo)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada para actualizar con id: " + id));

        existingCategory.setNombre(categoryDTO.getNombre());
        existingCategory.setTipo(categoryDTO.getTipo());
        // existingCategory.setDescripcion(categoryDTO.getDescripcion()); // Si lo tienes

        Category updatedCategory = categoryRepository.save(existingCategory);
        return convertToDTO(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada para eliminar con id: " + id);
        }
        // Aquí deberías añadir lógica para verificar si la categoría está en uso
        // por algún producto o servicio antes de permitir su eliminación.
        // Si está en uso, podrías lanzar una excepción o manejarlo según tu lógica de negocio.
        // Ejemplo: 
        // if (productRepository.existsByCategoryId(id) || serviceRepository.existsByCategoryId(id)) {
        //    throw new DataIntegrityViolationException("No se puede eliminar la categoría porque está en uso.");
        // }
        categoryRepository.deleteById(id);
    }
}