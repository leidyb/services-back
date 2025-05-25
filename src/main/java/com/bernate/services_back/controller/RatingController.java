package com.bernate.services_back.controller;

import com.bernate.services_back.dto.RatingDTO;
import com.bernate.services_back.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ratings") // Ruta base para calificaciones
@CrossOrigin(origins = "http://localhost:5173") // O tu config global
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * Crea una nueva calificación.
     * Requiere que el usuario esté autenticado.
     * La lógica en el servicio verifica que no califique sus propios productos/servicios
     * y que no califique dos veces lo mismo.
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()") // Cualquier usuario autenticado puede intentar calificar
    public ResponseEntity<RatingDTO> createRating(@Valid @RequestBody RatingDTO ratingDTO) {
        RatingDTO createdRating = ratingService.createRating(ratingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRating);
    }

    /**
     * Obtiene calificaciones paginadas para un producto específico.
     * Público.
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<RatingDTO>> getRatingsForProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) { // Menos calificaciones por página por defecto
        Page<RatingDTO> ratings = ratingService.getRatingsForProduct(productId, page, size);
        return ResponseEntity.ok(ratings);
    }

    /**
     * Obtiene calificaciones paginadas para un servicio específico.
     * Público.
     */
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<Page<RatingDTO>> getRatingsForService(
            @PathVariable Long serviceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<RatingDTO> ratings = ratingService.getRatingsForService(serviceId, page, size);
        return ResponseEntity.ok(ratings);
    }

    /**
     * Obtiene calificaciones paginadas hechas por un usuario específico.
     * Podría ser público o protegido dependiendo de tus reglas de privacidad.
     * Por ahora, lo dejamos protegido para el propio usuario o un admin.
     */
    @GetMapping("/user/{raterId}")
    @PreAuthorize("isAuthenticated() and (#raterId == principal.id or hasRole('ADMIN'))")
    public ResponseEntity<Page<RatingDTO>> getRatingsByRater(
            @PathVariable Long raterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RatingDTO> ratings = ratingService.getRatingsByRater(raterId, page, size);
        return ResponseEntity.ok(ratings);
    }

    /**
     * Elimina una calificación.
     * Solo el creador de la calificación o un ADMIN pueden eliminarla.
     * La lógica de permiso está en el servicio, pero también podemos poner @PreAuthorize.
     */
    @DeleteMapping("/{ratingId}")
    @PreAuthorize("isAuthenticated()") // El servicio verificará si es el dueño o admin
    public ResponseEntity<Void> deleteRating(@PathVariable Long ratingId) {
        ratingService.deleteRating(ratingId);
        return ResponseEntity.noContent().build();
    }
}