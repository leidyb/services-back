package com.bernate.services_back.service;

import com.bernate.services_back.dto.ServiceDTO;
import com.bernate.services_back.exception.ResourceNotFoundException;
import com.bernate.services_back.model.Category;
import com.bernate.services_back.model.CategoryType;
import com.bernate.services_back.model.ServiceEntity;
import com.bernate.services_back.model.User;
import com.bernate.services_back.repository.CategoryRepository;
import com.bernate.services_back.repository.ServiceRepository;
import com.bernate.services_back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final Path serviceImageRootLocation;

    private final String baseUploadURLPath = "/uploads";
    public static final String SERVICE_IMAGE_SUBDIRECTORY = "service-images";

    @Autowired
    public ServiceService(ServiceRepository serviceRepository,
                          CategoryRepository categoryRepository,
                          UserRepository userRepository,
                          @Value("${app.upload.dir:./uploads_default}") String uploadDir) {
        this.serviceRepository = serviceRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;


        this.serviceImageRootLocation = Paths.get(uploadDir, SERVICE_IMAGE_SUBDIRECTORY).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.serviceImageRootLocation);
            System.out.println("Directorio de almacenamiento de imágenes de servicio inicializado en: " + this.serviceImageRootLocation.toString());
        } catch (IOException e) {
            System.err.println("No se pudo inicializar el directorio de imágenes de servicios: " + this.serviceImageRootLocation.toString());
            throw new RuntimeException("No se pudo inicializar el directorio de imágenes de servicios", e);
        }
    }


    private String storeServiceImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        if (!extension.matches("\\.(jpeg|jpg|png|gif)$")) {
             throw new RuntimeException("Formato de archivo no permitido para la imagen del servicio.");
        }

        String uniqueFilename = UUID.randomUUID().toString() + extension;

        try {
            Path destinationFile = this.serviceImageRootLocation.resolve(uniqueFilename).normalize();
            if (!destinationFile.getParent().equals(this.serviceImageRootLocation)) {
                throw new RuntimeException("No se puede guardar el archivo fuera del directorio raíz de servicios.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Falló al guardar el archivo de imagen del servicio " + uniqueFilename, e);
        }
    }

    private ServiceDTO convertToDTO(ServiceEntity serviceEntity) {
        String imageUrl = null;
        if (serviceEntity.getImagenes() != null && !serviceEntity.getImagenes().isEmpty()) {
            imageUrl = baseUploadURLPath + "/" + SERVICE_IMAGE_SUBDIRECTORY + "/" + serviceEntity.getImagenes();
        }
        return new ServiceDTO(
                serviceEntity.getId(), serviceEntity.getName(), serviceEntity.getDescription(),
                serviceEntity.getEstimatedPrice(), imageUrl, serviceEntity.getEstado(),
                serviceEntity.getCategoria() != null ? serviceEntity.getCategoria().getNombre() : null,
                serviceEntity.getOfertadoPor() != null ? serviceEntity.getOfertadoPor().getUsername() : null
        );
    }

    private ServiceEntity convertToEntity(ServiceDTO serviceDTO, ServiceEntity serviceToUpdate) {
        ServiceEntity serviceEntity = (serviceToUpdate == null) ? new ServiceEntity() : serviceToUpdate;
        serviceEntity.setName(serviceDTO.getName());
        serviceEntity.setDescription(serviceDTO.getDescription());
        serviceEntity.setEstimatedPrice(serviceDTO.getEstimatedPrice());

        serviceEntity.setEstado(serviceDTO.getEstado());

        if (serviceDTO.getCategoryName() != null && !serviceDTO.getCategoryName().trim().isEmpty()) {
            Category category = categoryRepository.findByNombreAndTipo(serviceDTO.getCategoryName(), CategoryType.SERVICIO)
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría de tipo SERVICIO '" + serviceDTO.getCategoryName() + "' no encontrada."));
            serviceEntity.setCategoria(category);
        } else {
            if (serviceToUpdate == null) {
                throw new IllegalArgumentException("La categoría es obligatoria para el servicio.");
            }
        }
        return serviceEntity;
    }

    @Transactional(readOnly = true)
    public Page<ServiceDTO> getAllServices(String searchTerm, int page, int size) {
         Pageable pageable = PageRequest.of(page, size);
        Page<ServiceEntity> servicesPage;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            servicesPage = serviceRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoriaNombreContainingIgnoreCase(
                    searchTerm, searchTerm, searchTerm, pageable);
        } else {
            servicesPage = serviceRepository.findAll(pageable);
        }
        return servicesPage.map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public ServiceDTO getServiceById(Long id) {
        ServiceEntity serviceEntity = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + id));
        return convertToDTO(serviceEntity);
    }


    @Transactional
    public ServiceDTO createService(ServiceDTO serviceDTO, MultipartFile imageFile) {
        ServiceEntity serviceEntity = convertToEntity(serviceDTO, null);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario ofertante no encontrado: " + authentication.getName()));
        serviceEntity.setOfertadoPor(currentUser);

        String filename = storeServiceImage(imageFile);
        if (filename != null) {
            serviceEntity.setImagenes(filename);
        }

        serviceEntity.setId(null);
        ServiceEntity savedService = serviceRepository.save(serviceEntity);
        return convertToDTO(savedService);
    }


    @Transactional
    public ServiceDTO updateService(Long id, ServiceDTO serviceDetailsDTO, MultipartFile imageFile) {
        ServiceEntity existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado para actualizar con id: " + id));

        convertToEntity(serviceDetailsDTO, existingService);

        if (imageFile != null && !imageFile.isEmpty()) {
            if (existingService.getImagenes() != null && !existingService.getImagenes().isEmpty()) {
                try {
                    Files.deleteIfExists(this.serviceImageRootLocation.resolve(existingService.getImagenes()));
                } catch (IOException e) { System.err.println("Error al eliminar imagen antigua del servicio: " + e.getMessage()); }
            }
            String filename = storeServiceImage(imageFile);
            existingService.setImagenes(filename);
        } else if (serviceDetailsDTO.getImagenes() == null || serviceDetailsDTO.getImagenes().isEmpty()) {
            if (existingService.getImagenes() != null && !existingService.getImagenes().isEmpty()) {
                 try {
                    Files.deleteIfExists(this.serviceImageRootLocation.resolve(existingService.getImagenes()));
                    existingService.setImagenes(null); 
                } catch (IOException e) { System.err.println("Error al eliminar imagen antigua del servicio: " + e.getMessage()); }
            }
        }





        ServiceEntity updatedService = serviceRepository.save(existingService);
        return convertToDTO(updatedService);
    }

    @Transactional
    public void deleteService(Long id) {
        ServiceEntity serviceEntity = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado para eliminar con id: " + id));

        if (serviceEntity.getImagenes() != null && !serviceEntity.getImagenes().isEmpty()) {
            try {
                Files.deleteIfExists(this.serviceImageRootLocation.resolve(serviceEntity.getImagenes()));
            } catch (IOException e) { System.err.println("Error al eliminar archivo de imagen del servicio: " + e.getMessage()); }
        }
        serviceRepository.deleteById(id);
    }
}