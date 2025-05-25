package com.bernate.services_back.service;

import com.bernate.services_back.dto.CategoryDTO;
import com.bernate.services_back.model.Category;
import com.bernate.services_back.model.CategoryType;
import com.bernate.services_back.repository.CategoryRepository;
import com.bernate.services_back.exception.ResourceNotFoundException;
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


    private CategoryDTO convertToDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getNombre(),
                category.getTipo()

        );
    }


    private Category convertToEntity(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setNombre(categoryDTO.getNombre());
        category.setTipo(categoryDTO.getTipo());

        return category;
    }



    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {





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


        Category updatedCategory = categoryRepository.save(existingCategory);
        return convertToDTO(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada para eliminar con id: " + id);
        }







        categoryRepository.deleteById(id);
    }
}