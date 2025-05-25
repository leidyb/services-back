package com.bernate.services_back.service;

import com.bernate.services_back.dto.ProductDTO;
import com.bernate.services_back.exception.ResourceNotFoundException;
import com.bernate.services_back.model.Category;
import com.bernate.services_back.model.Product;
import com.bernate.services_back.model.User; // Importar User
import com.bernate.services_back.repository.CategoryRepository;
import com.bernate.services_back.repository.ProductRepository;
import com.bernate.services_back.repository.UserRepository; // Importar UserRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication; // Para obtener el usuario autenticado
import org.springframework.security.core.context.SecurityContextHolder; // Para obtener el usuario autenticado
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Para usuario no encontrado
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value; // Para leer de application.properties
import org.springframework.web.multipart.MultipartFile; // Para manejar subida de archivos
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID; // Para nombres de archivo únicos

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final Path rootLocation; // Ruta raíz donde se guardan las imágenes de productos
    @Value("${app.upload.dir:${user.home}/services_back_uploads}") // Inyecta la ruta base de uploads
    private String baseUploadDir;

    public static final String PRODUCT_IMAGE_SUBPATH = "product-images"; // Subdirectorio específico

    @Autowired
    public ProductService(ProductRepository productRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            @Value("${app.upload.dir:${user.home}/services_back_uploads}") String uploadDir) { // Inyectar aquí también
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        // Construir la ruta completa al directorio de imágenes de productos
        this.rootLocation = Paths.get(uploadDir, PRODUCT_IMAGE_SUBPATH);
        try {
            Files.createDirectories(this.rootLocation); // Crea el directorio si no existe
        } catch (IOException e) {
            throw new RuntimeException(
                    "No se pudo inicializar el directorio de almacenamiento de imágenes de productos", e);
        }
    }

    // Dentro de ProductService.java
private String storeFile(MultipartFile file) {
    if (file == null || file.isEmpty()) { // Verificación de null para file
        return null; 
    }
    String originalFilename = file.getOriginalFilename();
    // Sanitize originalFilename para evitar problemas de path traversal si se usa directamente
    // Aunque estamos generando un UUID, es buena práctica si alguna vez se usa el original.
    // String safeOriginalFilename = StringUtils.cleanPath(originalFilename);

    String extension = "";
    if (originalFilename != null && originalFilename.contains(".")) {
        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
    }
    String uniqueFilename = UUID.randomUUID().toString() + extension;

    try {
        Path destinationFile = this.rootLocation.resolve(uniqueFilename).normalize().toAbsolutePath();
        Path rootLocationAbsolute = this.rootLocation.toAbsolutePath().normalize();

        // --- LOGS PARA DEPURACIÓN ---
        System.out.println("Root Location (absolute, normalized): " + rootLocationAbsolute);
        System.out.println("Destination File (absolute, normalized): " + destinationFile);
        System.out.println("Parent of Destination File: " + destinationFile.getParent());
        // --- FIN LOGS ---

        if (!destinationFile.getParent().equals(rootLocationAbsolute)) {
            System.err.println("ALERTA DE SEGURIDAD: Intento de guardar archivo fuera del directorio raíz.");
            System.err.println("Parent de Destino: " + destinationFile.getParent());
            System.err.println("Root Location Esperada: " + rootLocationAbsolute);
            throw new RuntimeException("No se puede guardar el archivo fuera del directorio raíz especificado. Destino: " + destinationFile.toString());
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return uniqueFilename;
    } catch (IOException e) {
        throw new RuntimeException("Falló al guardar el archivo " + uniqueFilename, e);
    }
}

    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productsPage;

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            productsPage = productRepository
                    .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoriaNombreContainingIgnoreCase(
                            searchTerm, searchTerm, searchTerm, pageable);
        } else {
            productsPage = productRepository.findAll(pageable);
        }
        return productsPage.map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        return convertToDTO(product);
    }

    @Transactional
    // Ahora createProduct necesita el MultipartFile
    public ProductDTO createProduct(ProductDTO productDTO, MultipartFile imageFile) {
        Product product = convertToEntity(productDTO, null);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario ofertante no encontrado: " + currentUsername));
        product.setOfertadoPor(currentUser);

        if (imageFile != null && !imageFile.isEmpty()) {
            String filename = storeFile(imageFile);
            product.setImagenes(filename); // Guardamos solo el nombre del archivo
        }

        product.setId(null); 
        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }


    @Transactional
    // updateProduct también necesitará el MultipartFile (opcional)
    public ProductDTO updateProduct(Long id, ProductDTO productDetailsDTO, MultipartFile imageFile) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado para actualizar con id: " + id));

        convertToEntity(productDetailsDTO, existingProduct); // Mapea campos del DTO

        if (imageFile != null && !imageFile.isEmpty()) {
            // Opcional: Eliminar la imagen antigua si existe
            if (existingProduct.getImagenes() != null && !existingProduct.getImagenes().isEmpty()) {
                try {
                    Path oldFilePath = rootLocation.resolve(existingProduct.getImagenes());
                    Files.deleteIfExists(oldFilePath);
                } catch (IOException e) {
                    // Loggear el error, pero no necesariamente detener la actualización
                    System.err.println("Error al eliminar imagen antigua: " + e.getMessage());
                }
            }
            String filename = storeFile(imageFile);
            existingProduct.setImagenes(filename);
        } else if (productDetailsDTO.getImagenes() == null || productDetailsDTO.getImagenes().isEmpty()) {
            // Si en el DTO 'imagenes' viene vacío o null, y queremos eliminar la imagen existente
            if (existingProduct.getImagenes() != null && !existingProduct.getImagenes().isEmpty()) {
                 try {
                    Path oldFilePath = rootLocation.resolve(existingProduct.getImagenes());
                    Files.deleteIfExists(oldFilePath);
                    existingProduct.setImagenes(null); // Limpiar el campo en la BD
                } catch (IOException e) {
                    System.err.println("Error al eliminar imagen antigua: " + e.getMessage());
                }
            }
        }
        // Si productDetailsDTO.getImagenes() tiene un valor pero imageFile es null, 
        // significa que el usuario no subió una nueva imagen pero no quiere borrar la existente.
        // En ese caso, no tocamos existingProduct.getImagenes() a menos que se indique explícitamente.

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado para eliminar con id: " + id));

        // Eliminar imagen asociada si existe
        if (product.getImagenes() != null && !product.getImagenes().isEmpty()) {
            try {
                Path filePath = rootLocation.resolve(product.getImagenes());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Loggear el error pero continuar con la eliminación del producto de la BD
                System.err.println("Error al eliminar archivo de imagen: " + e.getMessage());
            }
        }
        productRepository.deleteById(id);
    }

    // --- Métodos Privados de Mapeo ---
    private ProductDTO convertToDTO(Product product) {
        String imageUrl = null;
        if (product.getImagenes() != null && !product.getImagenes().isEmpty()) {
            // Construimos la URL relativa que el frontend usará.
            // MvcConfig mapea /uploads/product-images/ a la carpeta física.
            imageUrl = "/uploads/" + PRODUCT_IMAGE_SUBPATH + "/" + product.getImagenes();
        }

        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                imageUrl, // Usamos la URL construida
                product.getEstado(),
                product.getCategoria() != null ? product.getCategoria().getNombre() : null,
                product.getOfertadoPor() != null ? product.getOfertadoPor().getUsername() : null
        );
    }


    // Modificamos convertToEntity para que pueda actualizar un producto existente o
    // crear uno nuevo
    private Product convertToEntity(ProductDTO productDTO, Product productToUpdate) {
        Product product = (productToUpdate == null) ? new Product() : productToUpdate;

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        // El campo 'imagenes' se maneja por separado con el archivo subido
        // Pero si el DTO trae una URL (ej. al editar y no cambiar imagen), la podríamos conservar
        if (productToUpdate != null && productDTO.getImagenes() != null && !productDTO.getImagenes().contains("/uploads/")) {
             // Si estamos actualizando y el DTO.imagenes NO es una URL de nuestro servidor,
             // y NO se subió un nuevo archivo, podríamos querer mantener la imagen existente.
             // Sin embargo, si es una URL externa, la entidad solo guarda el nombre del archivo.
             // Esto necesita una lógica más clara: ¿el DTO envía el nombre del archivo o la URL completa?
             // Por ahora, dejaremos que el método update maneje la lógica de si se sube un nuevo archivo.
        } else if (productToUpdate == null && productDTO.getImagenes() != null) {
             // Al crear, si el DTO trae 'imagenes' como string (URL externa), podríamos guardarla
             // pero nuestra lógica actual es para subir archivos.
             // Por ahora, la entidad 'imagenes' se poblará con el resultado de storeFile.
        }

        product.setEstado(productDTO.getEstado());

        if (productDTO.getCategoryName() != null && !productDTO.getCategoryName().trim().isEmpty()) {
            Category category = categoryRepository.findByNombre(productDTO.getCategoryName())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría '" + productDTO.getCategoryName() + "' no encontrada."));
            product.setCategoria(category);
        } else {
             if (productToUpdate == null) {
                 throw new IllegalArgumentException("El nombre de la categoría es obligatorio para el producto.");
             }
        }
        return product;
    }

}