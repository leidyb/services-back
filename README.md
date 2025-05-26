# Services Back - El Tejar (Backend)

Backend de la aplicación "El Tejar - Ventana de Servicios y Productos", desarrollado con Spring Boot. Esta API REST gestiona productos, servicios, usuarios, categorías y calificaciones.

## Descripción del Proyecto

Este proyecto sirve como el componente backend para una plataforma donde los usuarios pueden ofertar y encontrar tanto productos tangibles como servicios. Incluye funcionalidades de autenticación y autorización basadas en roles utilizando JWT, gestión de imágenes, y operaciones CRUD para todas las entidades principales.

## Características Principales

* **Gestión de Productos:** CRUD completo, búsqueda paginada, subida de imágenes.
* **Gestión de Servicios:** CRUD completo, búsqueda paginada, gestión de imágenes (si aplica).
* **Gestión de Categorías:** CRUD para categorías, diferenciando entre categorías de productos y servicios.
* **Gestión de Usuarios:** Registro, Login.
* **Seguridad:** Autenticación basada en JWT, autorización por roles (USER, ADMIN, PROVEEDOR - ajustar según tus roles).
* **Calificaciones:** Sistema para que los usuarios califiquen productos y servicios.
* **Perfiles de Vendedor:** Visualización de promedio de calificaciones para vendedores.
* **Administración:** Endpoints para gestión de usuarios y roles (accesibles por ADMIN).

## Tecnologías Utilizadas

* **Java 21** (o la versión que estés usando)
* **Spring Boot 3.4.5** (o la versión que estés usando)
    * Spring Web (para API REST)
    * Spring Data JPA (para persistencia de datos)
    * Spring Security (para autenticación y autorización)
* **JWT (JSON Web Tokens):** Para la autenticación stateless. Se usó la librería `io.jsonwebtoken` (jjwt).
* **Maven** (o Gradle, como gestor de dependencias y build)
* **Lombok:** Para reducir código boilerplate en entidades y DTOs.
* **Base de Datos:**
    * Desarrollo Local: PostgreSQL (o MySQL, según tu configuración final)
    * Producción (Render.com): PostgreSQL (o MySQL)
* **Validación:** Jakarta Bean Validation.
* **Subida de Archivos:** Manejo de `MultipartFile` para imágenes.

## Estructura del Proyecto (Backend)

El backend sigue una arquitectura en capas:

* **`config`**: Clases de configuración (Spring Security, MVC, CORS, etc.).
* **`controller`**: Controladores REST que manejan las peticiones HTTP.
    * `AuthController`: Endpoints para registro y login.
    * `ProductController`: Endpoints para la gestión de productos.
    * `ServiceController`: Endpoints para la gestión de servicios.
    * `CategoryController`: Endpoints para la gestión de categorías.
    * `RatingController`: Endpoints para la gestión de calificaciones.
    * `AdminController`: Endpoints para tareas administrativas (ej. gestión de roles).
    * `UserController`: Endpoints para perfiles públicos de usuario/vendedor.
* **`dto`**: Data Transfer Objects para la comunicación entre capas y con el cliente.
* **`exception`**: Clases para manejo de excepciones personalizadas y globales.
* **`model`**: Entidades JPA que representan la estructura de la base de datos.
    * `User.java`, `Product.java`, `ServiceEntity.java`, `Category.java`, `Rating.java`
    * Enums: `RolUsuario.java` (o `RolType`), `EstadoOferta.java`, `CategoryType.java`
* **`repository`**: Interfaces de Spring Data JPA para el acceso a datos.
* **`security`**: Clases relacionadas con Spring Security.
    * `jwt` (subpaquete): `JwtAuthEntryPoint.java`, `JwtAuthenticationFilter.java`, `JwtTokenProvider.java`
    * `CustomUserDetails.java` (si lo creaste)
* **`service`**: Clases de servicio que contienen la lógica de negocio.
    * `AuthService.java`, `ProductService.java`, `ServiceService.java`, `CategoryService.java`, `RatingService.java`
    * `UserDetailsServiceImpl.java`

## Configuración del Entorno (Desarrollo Local)

1.  **Prerrequisitos:**
    * JDK 21 (o la versión especificada en `pom.xml`).
    * Maven 3.x (o Gradle).
    * Base de datos PostgreSQL (o MySQL) instalada y corriendo.
    * Una herramienta para interactuar con la base de datos (ej. pgAdmin para PostgreSQL, MySQL Workbench para MySQL).

2.  **Base de Datos:**
    * Crea una base de datos en tu servidor PostgreSQL/MySQL. Por ejemplo: `services_tejar_db`.
    * Asegúrate de que el usuario de la base de datos tenga permisos para crear/modificar tablas en este esquema.

3.  **Configuración de la Aplicación (`application.properties`):**
    * Copia `src/main/resources/application.properties.example` a `src/main/resources/application.properties` (si tienes un archivo de ejemplo).
    * Modifica `application.properties` con los detalles de tu conexión a la base de datos local:
        ```properties
        spring.datasource.url=jdbc:postgresql://localhost:5432/services_tejar_db # O tu URL de MySQL
        spring.datasource.username=tu_usuario_db
        spring.datasource.password=tu_contraseña_db
        spring.datasource.driverClassName=org.postgresql.Driver # O com.mysql.cj.jdbc.Driver
        spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect # O MySQLDialect
        spring.jpa.hibernate.ddl-auto=update

        jwt.secret=ESTE_ES_UN_SECRETO_DE_DESARROLLO_MUY_LARGO_Y_SEGURO
        jwt.expiration.ms=3600000 # 1 hora

        # Para subida de imágenes (ajusta la ruta a tu sistema)
        app.upload.dir=./local_uploads 
        # Esta carpeta se creará en la raíz del proyecto al ejecutar la app
        ```

4.  **Ejecutar la Aplicación:**
    * Desde la línea de comandos en la raíz del proyecto:
        ```bash
        mvn spring-boot:run
        ```
    * O importa el proyecto en tu IDE (IntelliJ IDEA, Eclipse STS, VS Code) y ejecútalo desde allí.
    * La aplicación debería estar disponible en `http://localhost:8080`.

## Endpoints de la API (Resumen)

La API sigue el prefijo `/api`.

* **Autenticación (`/api/auth`):**
    * `POST /register`: Registrar un nuevo usuario.
    * `POST /login`: Iniciar sesión y obtener un token JWT.
* **Productos (`/api/v1/products`):**
    * `GET /`: Listar productos (paginado, con búsqueda).
    * `GET /{id}`: Obtener un producto por ID.
    * `POST /`: Crear un nuevo producto (requiere autenticación, multipart/form-data si hay imagen).
    * `PUT /{id}`: Actualizar un producto (requiere autenticación, multipart/form-data si hay imagen).
    * `DELETE /{id}`: Eliminar un producto (requiere rol ADMIN).
* **Servicios (`/api/v1/services`):**
    * `GET /`: Listar servicios (paginado, con búsqueda).
    * `GET /{id}`: Obtener un servicio por ID.
    * `POST /`: Crear un nuevo servicio (requiere autenticación, multipart/form-data si hay imagen).
    * `PUT /{id}`: Actualizar un servicio (requiere autenticación, multipart/form-data si hay imagen).
    * `DELETE /{id}`: Eliminar un servicio (requiere rol ADMIN).
* **Categorías (`/api/v1/categories`):**
    * `GET /`: Listar categorías (opcionalmente filtrar por `?tipo=PRODUCTO` o `?tipo=SERVICIO`).
    * `GET /{id}`: Obtener una categoría por ID.
    * `POST /`: Crear una nueva categoría (requiere rol ADMIN).
    * `PUT /{id}`: Actualizar una categoría (requiere rol ADMIN).
    * `DELETE /{id}`: Eliminar una categoría (requiere rol ADMIN).
* **Calificaciones (`/api/v1/ratings`):**
    * `POST /`: Crear una nueva calificación (requiere autenticación).
    * `GET /product/{productId}`: Listar calificaciones para un producto (paginado).
    * `GET /service/{serviceId}`: Listar calificaciones para un servicio (paginado).
    * `DELETE /{ratingId}`: Eliminar una calificación (por el creador o ADMIN).
* **Perfiles de Usuario/Vendedor (`/api/v1/users`):**
    * `GET /{username}/profile`: Obtener perfil público de un vendedor con promedio de calificaciones.
* **Administración (`/api/admin`):**
    * `GET /users`: Listar todos los usuarios (requiere rol ADMIN).
    * `POST /users/{username}/roles`: Asignar/actualizar roles a un usuario (requiere rol ADMIN).

## Despliegue en Render.com

Este proyecto está configurado para ser desplegado usando Docker en Render.com.
1.  Asegúrate de tener un `Dockerfile` en la raíz del proyecto.
2.  Configura un "Web Service" en Render, seleccionando "Docker" como el entorno.
3.  Configura las siguientes variables de entorno en Render:
    * `SPRING_PROFILES_ACTIVE=prod`
    * `DB_URL`: La URL interna de tu base de datos PostgreSQL/MySQL en Render (incluyendo `?sslmode=require` si es necesario para PostgreSQL).
    * `DB_USERNAME`: Tu usuario de la base de datos en Render.
    * `DB_PASSWORD`: Tu contraseña de la base de datos en Render.
    * `JWT_SECRET`: Una clave secreta fuerte para producción.
    * `APP_UPLOAD_DIR`: La ruta de montaje de tu disco persistente en Render (ej. `/var/data/uploads`).
4.  Configura un "Persistent Disk" en Render y móntalo en la ruta especificada en `APP_UPLOAD_DIR` para la persistencia de imágenes.

## Frontend
El frontend correspondiente a esta API se encuentra en un repositorio separado o en una carpeta diferente, desarrollado con React. Se debe configurar para que apunte a la URL de este backend desplegado.


---
