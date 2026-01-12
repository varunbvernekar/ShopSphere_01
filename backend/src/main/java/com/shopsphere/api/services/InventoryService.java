package com.shopsphere.api.services;

import com.shopsphere.api.dto.requestDTO.StockUpdateRequest;
import com.shopsphere.api.dto.responseDTO.InventoryResponse;

public interface InventoryService {
    InventoryResponse getInventory(String productId);

    InventoryResponse updateInventory(String productId, StockUpdateRequest request);

    void reduceStock(String productId, Integer quantity); // For internal use by OrderService

    void initializeInventory(String productId, Integer quantity, Integer threshold);

    void deleteInventory(String productId);
}
