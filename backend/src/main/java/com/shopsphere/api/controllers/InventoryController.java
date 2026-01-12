package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.StockUpdateRequest;
import com.shopsphere.api.dto.responseDTO.InventoryResponse;
import com.shopsphere.api.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable String productId) {
        return ResponseEntity.ok(inventoryService.getInventory(productId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    public ResponseEntity<InventoryResponse> updateInventory(@PathVariable String productId,
            @RequestBody StockUpdateRequest request) {
        return ResponseEntity.ok(inventoryService.updateInventory(productId, request));
    }
}
