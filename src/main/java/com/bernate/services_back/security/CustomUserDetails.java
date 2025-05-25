package com.bernate.services_back.security; // O el paquete donde esté tu User.java si lo pones allí

import com.bernate.services_back.model.User; // Tu entidad User
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String password; // Este será el hash de la contraseña
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public static CustomUserDetails build(User user) {
        Collection<? extends GrantedAuthority> authorities =
            (user.getRoles() == null || user.getRoles().trim().isEmpty()) ?
            Collections.emptyList() :
            Arrays.stream(user.getRoles().split(","))
                    .map(String::trim) // Quitar espacios alrededor de cada rol
                    .filter(role -> !role.isEmpty()) // Filtrar strings vacíos si hay comas extra
                    .map(SimpleGrantedAuthority::new) // Convertir cada string de rol a SimpleGrantedAuthority
                    .collect(Collectors.toList());

        return new CustomUserDetails(
                user.getId(), // Asumiendo que tu entidad User tiene un getter getId()
                user.getUsername(),
                user.getPassword(), // Asumiendo que tu entidad User tiene un getter getPassword() para el hash
                authorities
        );
    }

    public Long getId() { // Getter para el ID, necesario para #principal.id en SpEL
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // Métodos restantes de UserDetails (puedes dejarlos como true por ahora,
    // o implementar lógica real si la necesitas para bloquear/deshabilitar cuentas)
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}