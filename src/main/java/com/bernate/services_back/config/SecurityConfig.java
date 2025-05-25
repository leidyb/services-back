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
import java.util.List;

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

        return (web) -> web.ignoring().requestMatchers(
            "/uploads/**", 
            "/images/**", 
            "/css/**", 
            "/js/**",
            "/favicon.ico",
            "/error"
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
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()



                .requestMatchers(HttpMethod.GET, "/api/v1/products", "/api/v1/products/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/products").hasAnyRole("USER", "ADMIN", "PROVEEDOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasAnyRole("USER", "ADMIN", "PROVEEDOR")// Ajusta roles
                .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasRole("ADMIN")


                .requestMatchers(HttpMethod.GET, "/api/v1/categories", "/api/v1/categories/**").permitAll()
                .requestMatchers("/api/v1/categories/**").hasRole("ADMIN") 


                .requestMatchers(HttpMethod.GET, "/api/v1/services", "/api/v1/services/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/services").hasAnyRole("USER", "ADMIN", "PROVEEDOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/services/**").hasAnyRole("USER", "ADMIN", "PROVEEDOR")// Ajusta roles
                .requestMatchers(HttpMethod.DELETE, "/api/v1/services/**").hasRole("ADMIN")


                .requestMatchers(HttpMethod.GET, "/api/v1/ratings/product/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/ratings/service/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/ratings").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/ratings/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/ratings/user/**").authenticated()


                .requestMatchers(HttpMethod.GET, "/api/v1/users/*/profile").permitAll()


                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173")); 
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}