package com.bernate.services_back.repository;

import com.bernate.services_back.model.Rating;
import com.bernate.services_back.model.User; // Asegúrate de tener esta importación
// No necesitas Product o ServiceEntity aquí si buscas por ID
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Page<Rating> findByProductId(Long productId, Pageable pageable);
    Page<Rating> findByServiceId(Long serviceId, Pageable pageable);
    Page<Rating> findByRaterId(Long raterId, Pageable pageable);

    // --- ASEGÚRATE DE QUE ESTOS MÉTODOS ESTÉN DEFINIDOS ASÍ ---
    // Spring Data JPA entenderá que quieres buscar un Rating donde
    // el campo 'rater' (que es un User) coincida con el User provisto
    // Y donde el campo 'product' (que es un Product) tenga un 'id' que coincida con el Long provisto.
    boolean existsByRaterAndProduct_Id(User rater, Long productId);
    boolean existsByRaterAndService_Id(User rater, Long serviceId);
    // --- FIN DE MÉTODOS A ASEGURAR ---

    @Query("SELECT COALESCE(AVG(r.score), 0.0) FROM Rating r WHERE r.product.ofertadoPor = :provider")
    Double getAverageRatingForProviderProducts(@Param("provider") User provider);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.product.ofertadoPor = :provider")
    Long countRatingsForProviderProducts(@Param("provider") User provider);

    @Query("SELECT COALESCE(AVG(r.score), 0.0) FROM Rating r WHERE r.service.ofertadoPor = :provider")
    Double getAverageRatingForProviderServices(@Param("provider") User provider);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.service.ofertadoPor = :provider")
    Long countRatingsForProviderServices(@Param("provider") User provider);

    List<Rating> findByProductOfertadoPor(User provider);
    List<Rating> findByServiceOfertadoPor(User provider);
}