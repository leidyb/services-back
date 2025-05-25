package com.bernate.services_back.service;

import com.bernate.services_back.dto.AuthResponse;
import com.bernate.services_back.dto.LoginRequest;
import com.bernate.services_back.dto.RegisterRequest;
import com.bernate.services_back.dto.UpdateUserRolesRequest;
import com.bernate.services_back.dto.UserResponseDTO;
import com.bernate.services_back.exception.ResourceNotFoundException;
import com.bernate.services_back.model.User;
import com.bernate.services_back.repository.UserRepository;
import com.bernate.services_back.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bernate.services_back.dto.SellerProfileDTO; // Nuevo DTO
import com.bernate.services_back.repository.RatingRepository; // Importar RatingRepository

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private final RatingRepository ratingRepository; // NUEVA INYECCIÓN

    @Autowired
    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            RatingRepository ratingRepository) { // AÑADIR AL CONSTRUCTOR
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.ratingRepository = ratingRepository; // ASIGNAR
    }

    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Error: El nombre de usuario ya está en uso!");
        }
        if (userRepository.existsByCorreo(registerRequest.getCorreo())) {
            throw new RuntimeException("Error: El correo electrónico ya está en uso!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setNombre(registerRequest.getNombre());
        user.setApellido(registerRequest.getApellido());
        user.setCorreo(registerRequest.getCorreo());

        // --- ASIGNAR NUEVOS CAMPOS OPCIONALES ---
        user.setTelefono(registerRequest.getTelefono());
        user.setUbicacion(registerRequest.getUbicacion());

        user.setRoles("ROLE_USER");

        return userRepository.save(user);
    }

    // ... (método authenticateUser y otros se mantienen igual) ...
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return new AuthResponse(jwt);
    }

    @Transactional
    public User updateUserRoles(String username, UpdateUserRolesRequest updateUserRolesRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username));

        List<String> newRoles = updateUserRolesRequest.getRoles();
        if (newRoles == null || newRoles.isEmpty()) {
            throw new IllegalArgumentException("La lista de roles no puede ser nula o vacía.");
        }
        String rolesAsString = newRoles.stream()
                .map(String::trim)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase())
                .distinct()
                .collect(Collectors.joining(","));
        user.setRoles(rolesAsString);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToUserResponseDTO)
                .collect(Collectors.toList());
    }

    private UserResponseDTO convertToUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRoles());
    }
    @Transactional(readOnly = true)
    public SellerProfileDTO getSellerProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado con username: " + username));

        Double avgProductRating = ratingRepository.getAverageRatingForProviderProducts(user);
        Long countProductRatings = ratingRepository.countRatingsForProviderProducts(user);
        Double avgServiceRating = ratingRepository.getAverageRatingForProviderServices(user);
        Long countServiceRatings = ratingRepository.countRatingsForProviderServices(user);

        long totalRatings = countProductRatings + countServiceRatings;
        double overallAvg = 0.0;
        if (totalRatings > 0) {
            // Promedio ponderado o simple. Aquí un promedio simple si ambos tienen calificaciones.
            // O podrías calcularlo sumando todos los scores y dividiendo por totalRatings.
            // Este es un promedio de los promedios, lo cual no es ideal si un tipo tiene muchas más calificaciones.
            // Una mejor forma sería obtener todos los scores y calcular un único promedio.
            // Por ahora, un cálculo más directo:
            double totalScoreSum = (avgProductRating * countProductRatings) + (avgServiceRating * countServiceRatings);
            overallAvg = totalScoreSum / totalRatings;
            if (Double.isNaN(overallAvg) || Double.isInfinite(overallAvg)) {
                overallAvg = 0.0; // Evitar NaN si no hay calificaciones
            }
        }

        // Redondear a 1 decimal
        overallAvg = Math.round(overallAvg * 10.0) / 10.0;
        avgProductRating = Math.round(avgProductRating * 10.0) / 10.0;
        avgServiceRating = Math.round(avgServiceRating * 10.0) / 10.0;


        return new SellerProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getNombre(),
                user.getApellido(),
                user.getUbicacion(), // Asumiendo que tu User tiene getUbicacion()
                avgProductRating,
                countProductRatings,
                avgServiceRating,
                countServiceRatings,
                overallAvg,
                totalRatings
        );
    }

}