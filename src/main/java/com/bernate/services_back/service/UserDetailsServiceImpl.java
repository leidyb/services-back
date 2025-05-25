package com.bernate.services_back.service; // O com.bernate.services_back.security

import com.bernate.services_back.model.User;
import com.bernate.services_back.repository.UserRepository;
import com.bernate.services_back.security.CustomUserDetails; // Importa tu CustomUserDetails
import org.slf4j.Logger; // Para logging (opcional pero recomendado)
import org.slf4j.LoggerFactory; // Para logging
import org.springframework.beans.factory.annotation.Autowired;
// Quitamos las importaciones de GrantedAuthority, SimpleGrantedAuthority, Arrays, Collections, Collectors
// ya que esa lógica ahora está centralizada en CustomUserDetails.build()
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class); // Opcional

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional // Es buena práctica para métodos que acceden a la BD
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Intentando cargar usuario por username: {}", username); // Log opcional

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado con nombre de usuario: {}", username);
                    return new UsernameNotFoundException("Usuario no encontrado con nombre de usuario: " + username);
                });

        // logger.info("Usuario encontrado: {}. Roles string: {}", user.getUsername(), user.getRoles()); // Log opcional

        // Delegamos la construcción de UserDetails (incluyendo la conversión de roles)
        // a nuestra clase CustomUserDetails.
        CustomUserDetails userDetails = CustomUserDetails.build(user);

        // logger.info("UserDetails construido para {}: Authorities: {}", userDetails.getUsername(), userDetails.getAuthorities()); // Log opcional

        return userDetails;
    }
}