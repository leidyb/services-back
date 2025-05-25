package com.bernate.services_back.config;

import com.bernate.services_back.security.jwt.JwtAuthEntryPoint;
import com.bernate.services_back.security.jwt.JwtAuthenticationFilter;
import com.bernate.services_back.service.UserDetailsServiceImpl; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List; // Importar List si usas List.of() para CORS

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) 
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Estas rutas serán completamente ignoradas por Spring Security
        return (web) -> web.ignoring().requestMatchers(
            "/uploads/**", 
            "/images/**", 
            "/css/**", 
            "/js/**",
            "/favicon.ico", // Común añadir favicon
            "/error" // Para la Whitelabel Error Page de Spring si no se maneja explícitamente
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()             // Endpoints de autenticación públicos
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Permitir todas las peticiones OPTIONS (pre-vuelo CORS)
                // La ruta /uploads/** ya está manejada por webSecurityCustomizer para ser ignorada por Spring Security

                // Reglas para Productos
                .requestMatchers(HttpMethod.GET, "/api/v1/products", "/api/v1/products/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/products").hasAnyRole("USER", "ADMIN", "PROVEEDOR") // Ajusta roles según tu lógica
                .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasAnyRole("USER", "ADMIN", "PROVEEDOR")// Ajusta roles
                .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasRole("ADMIN")

                // Reglas para Categorías
                .requestMatchers(HttpMethod.GET, "/api/v1/categories", "/api/v1/categories/**").permitAll()
                .requestMatchers("/api/v1/categories/**").hasRole("ADMIN") 

                // Reglas para Servicios
                .requestMatchers(HttpMethod.GET, "/api/v1/services", "/api/v1/services/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/services").hasAnyRole("USER", "ADMIN", "PROVEEDOR") // Ajusta roles
                .requestMatchers(HttpMethod.PUT, "/api/v1/services/**").hasAnyRole("USER", "ADMIN", "PROVEEDOR")// Ajusta roles
                .requestMatchers(HttpMethod.DELETE, "/api/v1/services/**").hasRole("ADMIN")

                // Reglas para Calificaciones (Ratings)
                .requestMatchers(HttpMethod.GET, "/api/v1/ratings/product/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/ratings/service/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/ratings").authenticated() // Solo usuarios logueados pueden crear
                .requestMatchers(HttpMethod.DELETE, "/api/v1/ratings/**").authenticated() // Autenticados (lógica fina en servicio/controlador)
                .requestMatchers(HttpMethod.GET, "/api/v1/ratings/user/**").authenticated() // Autenticados (lógica fina en controlador con @PreAuthorize)

                // Regla para Perfiles Públicos de Usuario/Vendedor
                .requestMatchers(HttpMethod.GET, "/api/v1/users/*/profile").permitAll() // El * es un comodín para {username}

                // Reglas para Administración de Usuarios (ya protegidas por @PreAuthorize, pero una capa extra)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated() // Todas las demás peticiones requieren autenticación
            );

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Orígenes permitidos. Para producción, sé más específico.
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); 
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // Incluye todos los métodos que uses
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type")); // Cabeceras que el frontend puede leer
        configuration.setAllowCredentials(true); // Importante para cookies o tokens enviados por el navegador
        configuration.setMaxAge(3600L); // Tiempo que el navegador puede cachear la respuesta pre-vuelo OPTIONS

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplicar esta configuración a todas las rutas
        return source;
    }
}