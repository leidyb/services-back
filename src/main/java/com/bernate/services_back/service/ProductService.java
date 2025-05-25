package com.bernate.services_back.service;

import com.bernate.services_back.dto.ProductDTO;
import com.bernate.services_back.exception.ResourceNotFoundException;
import com.bernate.services_back.model.Category;
import com.bernate.services_back.model.Product;
import com.bernate.services_back.model.User;
import com.bernate.services_back.repository.CategoryRepository;
import com.bernate.services_back.repository.ProductRepository;
import com.bernate.services_back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final Path rootLocation;
    @Value("${app.upload.dir:${user.home}/services_back_uploads}")
    private String baseUploadDir;

    public static final String PRODUCT_IMAGE_SUBPATH = "product-images";

    @Autowired
    public ProductService(ProductRepository productRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            @Value("${app.upload.dir:${user.home}/services_back_uploads}") String uploadDir) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;

        this.rootLocation = Paths.get(uploadDir, PRODUCT_IMAGE_SUBPATH);
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException(
                    "No se pudo inicializar el directorio de almacenamiento de imágenes de productos", e);
        }
    }


private String storeFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
        return null; 
    }
    String originalFilename = file.getOriginalFilename();




    String extension = "";
    if (originalFilename != null && originalFilename.contains(".")) {
        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
    }
    String uniqueFilename = UUID.randomUUID().toString() + extension;

    try {
        Path destinationFile = this.rootLocation.resolve(uniqueFilename).normalize().toAbsolutePath();
        Path rootLocationAbsolute = this.rootLocation.toAbsolutePath().normalize();


        System.out.println("Root Location (absolute, normalized): " + rootLocationAbsolute);
        System.out.println("Destination File (absolute, normalized): " + destinationFile);
        System.out.println("Parent of Destination File: " + destinationFile.getParent());


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

    public ProductDTO createProduct(ProductDTO productDTO, MultipartFile imageFile) {
        Product product = convertToEntity(productDTO, null);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario ofertante no encontrado: " + currentUsername));
        product.setOfertadoPor(currentUser);

        if (imageFile != null && !imageFile.isEmpty()) {
            String filename = storeFile(imageFile);
            product.setImagenes(filename);
        }

        product.setId(null); 
        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }


    @Transactional

    public ProductDTO updateProduct(Long id, ProductDTO productDetailsDTO, MultipartFile imageFile) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado para actualizar con id: " + id));

        convertToEntity(productDetailsDTO, existingProduct);

        if (imageFile != null && !imageFile.isEmpty()) {

            if (existingProduct.getImagenes() != null && !existingProduct.getImagenes().isEmpty()) {
                try {
                    Path oldFilePath = rootLocation.resolve(existingProduct.getImagenes());
                    Files.deleteIfExists(oldFilePath);
                } catch (IOException e) {

                    System.err.println("Error al eliminar imagen antigua: " + e.getMessage());
                }
            }
            String filename = storeFile(imageFile);
            existingProduct.setImagenes(filename);
        } else if (productDetailsDTO.getImagenes() == null || productDetailsDTO.getImagenes().isEmpty()) {

            if (existingProduct.getImagenes() != null && !existingProduct.getImagenes().isEmpty()) {
                 try {
                    Path oldFilePath = rootLocation.resolve(existingProduct.getImagenes());
                    Files.deleteIfExists(oldFilePath);
                    existingProduct.setImagenes(null);
                } catch (IOException e) {
                    System.err.println("Error al eliminar imagen antigua: " + e.getMessage());
                }
            }
        }




        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado para eliminar con id: " + id));


        if (product.getImagenes() != null && !product.getImagenes().isEmpty()) {
            try {
                Path filePath = rootLocation.resolve(product.getImagenes());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {

                System.err.println("Error al eliminar archivo de imagen: " + e.getMessage());
            }
        }
        productRepository.deleteById(id);
    }


    private ProductDTO convertToDTO(Product product) {
        String imageUrl = null;
        if (product.getImagenes() != null && !product.getImagenes().isEmpty()) {


            imageUrl = "/uploads/" + PRODUCT_IMAGE_SUBPATH + "/" + product.getImagenes();
        }

        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                imageUrl,
                product.getEstado(),
                product.getCategoria() != null ? product.getCategoria().getNombre() : null,
                product.getOfertadoPor() != null ? product.getOfertadoPor().getUsername() : null
        );
    }




    private Product convertToEntity(ProductDTO productDTO, Product productToUpdate) {
        Product product = (productToUpdate == null) ? new Product() : productToUpdate;

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());


        if (productToUpdate != null && productDTO.getImagenes() != null && !productDTO.getImagenes().contains("/uploads/")) {





        } else if (productToUpdate == null && productDTO.getImagenes() != null) {



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