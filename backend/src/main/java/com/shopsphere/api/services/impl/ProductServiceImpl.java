package com.shopsphere.api.services.impl;

import com.shopsphere.api.dto.requestDTO.ProductRequest;
import com.shopsphere.api.dto.responseDTO.ProductResponse;
import com.shopsphere.api.entity.Product;
import com.shopsphere.api.repositories.ProductRepository;
import com.shopsphere.api.services.InventoryService;
import com.shopsphere.api.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> {
                    ProductResponse response = ProductResponse.fromEntity(product);
                    // Enrich with inventory data
                    var inventory = inventoryService.getInventory(product.getProductId());
                    response.setStockLevel(inventory.getQuantity());
                    response.setReorderThreshold(inventory.getReorderThreshold());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductResponse> getProductById(String id) {
        return productRepository.findById(id)
                .map(product -> {
                    ProductResponse response = ProductResponse.fromEntity(product);
                    var inventory = inventoryService.getInventory(product.getProductId());
                    response.setStockLevel(inventory.getQuantity());
                    response.setReorderThreshold(inventory.getReorderThreshold());
                    return response;
                });
    }

    @Override
    @Transactional
    public ProductResponse saveProduct(ProductRequest productRequest) {
        Product product = ProductRequest.toEntity(productRequest);

        String generatedId = "P" + System.currentTimeMillis();
        product.setProductId(generatedId);

        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }

        Product savedProduct = productRepository.save(product);

        // Initialize Inventory
        inventoryService.initializeInventory(generatedId, productRequest.getStockLevel(),
                productRequest.getReorderThreshold());

        // Return response with inventory info
        ProductResponse response = ProductResponse.fromEntity(savedProduct);
        // The inventory details will be fetched by subsequent calls to getProductById
        // or getAllProducts
        // or if the client needs them immediately, they should call getInventory
        // separately.
        return response;
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(String id, ProductRequest productRequest) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Product updatedState = ProductRequest.toEntity(productRequest);
        updatedState.setProductId(id);

        Product savedProduct = productRepository.save(updatedState);

        // Update inventory if provided in request?
        // Usually specific inventory endpoint is better, but for "Edit Product" page
        // convenience:
        if (productRequest.getStockLevel() != null || productRequest.getReorderThreshold() != null) {
            var stockReq = new com.shopsphere.api.dto.requestDTO.StockUpdateRequest();
            stockReq.setQuantity(productRequest.getStockLevel());
            stockReq.setThreshold(productRequest.getReorderThreshold());
            inventoryService.updateInventory(id, stockReq);
        }

        ProductResponse response = ProductResponse.fromEntity(savedProduct);
        var inventory = inventoryService.getInventory(id);
        response.setStockLevel(inventory.getQuantity());
        response.setReorderThreshold(inventory.getReorderThreshold());
        return response;
    }

    @Override
    @Transactional
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
        inventoryService.deleteInventory(id);
    }
}
