package com.bernate.services_back.repository;

import com.bernate.services_back.model.Category;
import com.bernate.services_back.model.ServiceEntity;
import com.bernate.services_back.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {


    Page<ServiceEntity> findByCategoria(Category categoria, Pageable pageable);


    Page<ServiceEntity> findByOfertadoPor(User ofertadoPor, Pageable pageable);




    Page<ServiceEntity> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoriaNombreContainingIgnoreCase(
        String nameTerm,
        String descriptionTerm,
        String categoriaNombreTerm,
        Pageable pageable
    );








}