package com.bernate.services_back.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
// Si vas a usar Sets para relaciones futuras, importa java.util.Set;

// Enum para los roles, colócalo en el mismo paquete o en un subpaquete como com.bernate.services_back.model.enums
enum RolUsuario { // O el nombre que prefieras para tu Enum, ej. UserRoleType
    ROLE_CLIENTE,
    ROLE_PROVEEDOR,
    ROLE_ADMIN
    // Puedes añadir más roles según necesites
}

@Entity
@Table(name = "users") // Nombre de tabla en BD, puedes cambiarlo a "usuarios" si prefieres
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Mantenemos 'id' como nombre de variable Java por convención

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Column(nullable = false, unique = true, length = 50) // `username` es buena práctica
    private String username;

    @NotBlank(message = "El nombre propio no puede estar vacío")
    @Size(max = 45)
    @Column(nullable = false, length = 45)
    private String nombre; // Campo de tu diagrama

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(max = 45)
    @Column(nullable = false, length = 45)
    private String apellido; // Campo de tu diagrama

    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "Debe ser una dirección de correo electrónico válida")
    @Size(max = 45)
    @Column(nullable = false, unique = true, length = 45)
    private String correo; // Campo de tu diagrama, es bueno que sea único

    @Size(max = 45)
    @Column(length = 45)
    private String telefono; // Campo de tu diagrama

    @NotBlank(message = "La contraseña no puede estar vacía")
    // El @Size aquí se refiere al hash, no a la contraseña original que ingresa el usuario.
    // El hash de BCrypt tiene una longitud fija (usualmente 60 caracteres).
    @Column(nullable = false, length = 100) // Mapea a tu columna "contraseña" (sin ñ)
    private String password; // Contraseña hasheada

    // Opción 1: Usar un String para roles (como lo tienes)
    @Column(length = 100)
    private String roles; // Ej: "ROLE_CLIENTE,ROLE_ADMIN"

    // Opción 2: Usar un ENUM para un solo rol (más restrictivo pero seguro para tipos)
    // Si un usuario solo puede tener UN rol.
    // @Enumerated(EnumType.STRING)
    // @Column(length = 20, name = "rol") // Mapea a tu columna "rol"
    // private RolUsuario rol;

    // Si quisieras múltiples roles usando ENUMs y una tabla de unión, sería más complejo (Many-to-Many).
    // Para empezar, String roles está bien si lo manejas consistentemente.

    @Size(max = 100) // Ajusta longitud según necesidad
    @Column(length = 100)
    private String ubicacion; // Campo de tu diagrama

    // Constructores
    public User(String username, String nombre, String apellido, String correo, String telefono, String password, String roles, String ubicacion) {
        this.username = username;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.password = password; // Asume que ya viene hasheada o se hashea en el servicio
        this.roles = roles;       // Ej: "ROLE_CLIENTE" o "ROLE_CLIENTE,ROLE_ADMIN"
        this.ubicacion = ubicacion;
    }

    // Constructor más simple para registro (sin ID, roles por defecto en el servicio)
     public User(String username, String password) {
        this.username = username;
        this.password = password;
        // this.roles = "ROLE_CLIENTE"; // El rol por defecto se asigna mejor en el servicio de registro
    }

    // Constructor para el AuthService que tenías (ajustado)
    public User(String username, String password, String roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }
}