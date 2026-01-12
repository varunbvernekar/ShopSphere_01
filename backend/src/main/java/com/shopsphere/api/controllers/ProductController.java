package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.ProductRequest;
import com.shopsphere.api.dto.responseDTO.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final com.shopsphere.api.services.ProductService productService;
    private final com.shopsphere.api.services.FileStorageService fileStorageService;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<ProductResponse> createProductWithImage(
            @RequestPart("product") String productJson,
            @RequestPart(value = "image", required = false) org.springframework.web.multipart.MultipartFile image)
            throws java.io.IOException {
        ProductRequest productRequest = objectMapper.readValue(productJson, ProductRequest.class);

        if (image != null && !image.isEmpty()) {
            String imagePath = fileStorageService.saveFile(image);
            productRequest.setPreviewImage(imagePath);
        }

        return ResponseEntity.ok(productService.saveProduct(productRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> saveProduct(@RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(productService.saveProduct(productRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String id,
            @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(productService.updateProduct(id, productRequest));
    }
}
