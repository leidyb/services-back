package com.bernate.services_back.controller;

import com.bernate.services_back.dto.ServiceDTO;
import com.bernate.services_back.exception.ResourceNotFoundException;
import com.bernate.services_back.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/services")
@CrossOrigin(origins = "http://localhost:5173")
public class ServiceController {

    private final ServiceService serviceService;

    @Autowired
    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public ResponseEntity<Page<ServiceDTO>> getAllServices(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ServiceDTO> servicesPage = serviceService.getAllServices(search, page, size);
        return ResponseEntity.ok(servicesPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getServiceById(@PathVariable Long id) {
        ServiceDTO serviceDTO = serviceService.getServiceById(id);
        return ResponseEntity.ok(serviceDTO);
    }


    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'PROVEEDOR')")
    public ResponseEntity<ServiceDTO> createService(
            @Valid @RequestPart("service") ServiceDTO serviceDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        ServiceDTO createdService = serviceService.createService(serviceDTO, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
    }


    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'PROVEEDOR')")
    public ResponseEntity<ServiceDTO> updateService(
            @PathVariable Long id,
            @Valid @RequestPart("service") ServiceDTO serviceDetailsDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            ServiceDTO updatedService = serviceService.updateService(id, serviceDetailsDTO, imageFile);
            return ResponseEntity.ok(updatedService);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        try {
            serviceService.deleteService(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}