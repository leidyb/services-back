package com.bernate.services_back.service;

import com.bernate.services_back.dto.RatingDTO;
import com.bernate.services_back.exception.BadRequestException;
import com.bernate.services_back.exception.ResourceNotFoundException;
import com.bernate.services_back.model.*;
import com.bernate.services_back.repository.ProductRepository;
import com.bernate.services_back.repository.RatingRepository;
import com.bernate.services_back.repository.ServiceRepository;
import com.bernate.services_back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ServiceRepository serviceRepository;

    @Autowired
    public RatingService(RatingRepository ratingRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            ServiceRepository serviceRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.serviceRepository = serviceRepository;
    }

    private RatingDTO convertToDTO(Rating rating) {
        return new RatingDTO(
                rating.getId(),
                rating.getScore(),
                rating.getComment(),
                rating.getCreatedAt(),
                rating.getRater() != null ? rating.getRater().getUsername() : null,
                rating.getProduct() != null ? rating.getProduct().getId() : null,
                rating.getProduct() != null ? rating.getProduct().getName() : null,
                rating.getService() != null ? rating.getService().getId() : null,
                rating.getService() != null ? rating.getService().getName() : null,
                determineProviderUserId(rating));
    }

    private Long determineProviderUserId(Rating rating) {
        if (rating.getProduct() != null && rating.getProduct().getOfertadoPor() != null) {
            return rating.getProduct().getOfertadoPor().getId();
        } else if (rating.getService() != null && rating.getService().getOfertadoPor() != null) {
            return rating.getService().getOfertadoPor().getId();
        }
        return null;
    }

    @Transactional
    public RatingDTO createRating(RatingDTO ratingDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new BadRequestException("El usuario debe estar autenticado para calificar.");
        }
        String currentUsername = authentication.getName();
        User rater = userRepository.findByUsername(currentUsername)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Usuario calificador no encontrado: " + currentUsername));

        Product product = null;
        ServiceEntity service = null;

        if (ratingDTO.getProductId() == null && ratingDTO.getServiceId() == null) {
            throw new BadRequestException("Se debe especificar un ID de producto o de servicio para calificar.");
        }
        if (ratingDTO.getProductId() != null && ratingDTO.getServiceId() != null) {
            throw new BadRequestException("Solo se puede calificar un producto o un servicio a la vez, no ambos.");
        }

        if (ratingDTO.getProductId() != null) {
            product = productRepository.findById(ratingDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con id: " + ratingDTO.getProductId()));
            if (product.getOfertadoPor().getId().equals(rater.getId())) {
                throw new BadRequestException("No puedes calificar tus propios productos.");
            }
            if (ratingRepository.existsByRaterAndProduct_Id(rater, ratingDTO.getProductId())) {
                throw new BadRequestException("Ya has calificado este producto anteriormente.");
            }
        } else {
            service = serviceRepository.findById(ratingDTO.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Servicio no encontrado con id: " + ratingDTO.getServiceId()));
            if (service.getOfertadoPor().getId().equals(rater.getId())) {
                throw new BadRequestException("No puedes calificar tus propios servicios.");
            }
            if (ratingRepository.existsByRaterAndService_Id(rater, ratingDTO.getServiceId())) {
                throw new BadRequestException("Ya has calificado este servicio anteriormente.");
            }
        }

        Rating rating = new Rating(
                rater,
                ratingDTO.getScore(),
                ratingDTO.getComment(),
                product,
                service);

        Rating savedRating = ratingRepository.save(rating);
        return convertToDTO(savedRating);
    }

    @Transactional(readOnly = true)
    public Page<RatingDTO> getRatingsForProduct(Long productId, int page, int size) {

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + productId);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Rating> ratingsPage = ratingRepository.findByProductId(productId, pageable);
        return ratingsPage.map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<RatingDTO> getRatingsForService(Long serviceId, int page, int size) {

        if (!serviceRepository.existsById(serviceId)) {
            throw new ResourceNotFoundException("Servicio no encontrado con id: " + serviceId);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Rating> ratingsPage = ratingRepository.findByServiceId(serviceId, pageable);
        return ratingsPage.map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<RatingDTO> getRatingsByRater(Long raterId, int page, int size) {
        if (!userRepository.existsById(raterId)) {
            throw new ResourceNotFoundException("Usuario calificador no encontrado con id: " + raterId);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Rating> ratingsPage = ratingRepository.findByRaterId(raterId, pageable);
        return ratingsPage.map(this::convertToDTO);
    }

    @Transactional
    public void deleteRating(Long ratingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new BadRequestException("El usuario debe estar autenticado para eliminar calificaciones.");
        }
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + currentUsername));

        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación no encontrada con id: " + ratingId));

        boolean isAdmin = currentUser.getRoles() != null && currentUser.getRoles().contains("ROLE_ADMIN");
        if (!rating.getRater().getId().equals(currentUser.getId()) && !isAdmin) {
            throw new BadRequestException("No tienes permiso para eliminar esta calificación.");
        }
        ratingRepository.delete(rating);
    }
}