package com.shopsphere.api.services;

import com.shopsphere.api.dto.requestDTO.ProductRequest;
import com.shopsphere.api.dto.responseDTO.ProductResponse;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductResponse> getAllProducts();

    Optional<ProductResponse> getProductById(String id);

    ProductResponse saveProduct(ProductRequest productRequest);

    ProductResponse updateProduct(String id, ProductRequest productRequest);

    void deleteProduct(String id);
    // Removed direct stock update methods, now handled by InventoryService
}
