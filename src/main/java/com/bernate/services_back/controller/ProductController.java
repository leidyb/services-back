package com.bernate.services_back.controller;

import com.bernate.services_back.dto.ProductDTO;
import com.bernate.services_back.exception.ResourceNotFoundException;
import com.bernate.services_back.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;




@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) { /* ... sin cambios ... */
        Page<ProductDTO> productsPage = productService.getAllProducts(search, page, size);
        return ResponseEntity.ok(productsPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) { /* ... sin cambios ... */
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }


    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestPart("product") ProductDTO productDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        ProductDTO createdProduct = productService.createProduct(productDTO, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestPart("product") ProductDTO productDetailsDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            ProductDTO updatedProduct = productService.updateProduct(id, productDetailsDTO, imageFile);
            return ResponseEntity.ok(updatedProduct);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) { /* ... sin cambios ... */
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}