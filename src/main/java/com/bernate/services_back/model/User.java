package com.bernate.services_back.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;



enum RolUsuario {
    ROLE_CLIENTE,
    ROLE_PROVEEDOR,
    ROLE_ADMIN

}

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "El nombre propio no puede estar vacío")
    @Size(max = 45)
    @Column(nullable = false, length = 45)
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(max = 45)
    @Column(nullable = false, length = 45)
    private String apellido;

    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "Debe ser una dirección de correo electrónico válida")
    @Size(max = 45)
    @Column(nullable = false, unique = true, length = 45)
    private String correo;

    @Size(max = 45)
    @Column(length = 45)
    private String telefono;

    @NotBlank(message = "La contraseña no puede estar vacía")


    @Column(nullable = false, length = 100)
    private String password;


    @Column(length = 100)
    private String roles;










    @Size(max = 100)
    @Column(length = 100)
    private String ubicacion;


    public User(String username, String nombre, String apellido, String correo, String telefono, String password, String roles, String ubicacion) {
        this.username = username;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.password = password;
        this.roles = roles;
        this.ubicacion = ubicacion;
    }


     public User(String username, String password) {
        this.username = username;
        this.password = password;

    }


    public User(String username, String password, String roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }
}