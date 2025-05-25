package com.bernate.services_back.security.jwt; // Ajusta el paquete si es necesario

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());
        // Aquí podrías enviar un JSON más detallado si quisieras.
        // Por ahora, solo un 401 con el mensaje de la excepción.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized - " + authException.getMessage());
    }
}