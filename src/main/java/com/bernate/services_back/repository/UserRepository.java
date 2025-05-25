package com.bernate.services_back.repository;

import com.bernate.services_back.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);

    // --- NUEVO MÉTODO SUGERIDO ---
    Boolean existsByCorreo(String correo); // Para verificar si el correo ya está en uso
}