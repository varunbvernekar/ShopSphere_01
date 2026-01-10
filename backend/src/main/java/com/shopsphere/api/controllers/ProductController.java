package com.shopsphere.api.controllers;

import com.shopsphere.api.entity.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // For development
public class ProductController {

    private final com.shopsphere.api.services.ProductService productService;
    private final com.shopsphere.api.services.FileStorageService fileStorageService;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supports both JSON and Multipart (for images)
     */
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<Product> createProductWithImage(
            @RequestPart("product") String productJson,
            @RequestPart(value = "image", required = false) org.springframework.web.multipart.MultipartFile image)
            throws java.io.IOException {
        Product product = objectMapper.readValue(productJson, Product.class);

        if (image != null && !image.isEmpty()) {
            String imagePath = fileStorageService.saveFile(image);
            product.setPreviewImage(imagePath);
        }

        return ResponseEntity.ok(productService.saveProduct(product));
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.saveProduct(product)); // saveProduct usually handles create/update if
                                                                       // ID exists
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(@PathVariable String id, @RequestBody Integer stockLevel) {
        return ResponseEntity.ok(productService.updateStock(id, stockLevel));
    }

    @PatchMapping("/{id}/threshold")
    public ResponseEntity<Product> updateThreshold(@PathVariable String id, @RequestBody Integer threshold) {
        return ResponseEntity.ok(productService.updateThreshold(id, threshold));
    }
}
