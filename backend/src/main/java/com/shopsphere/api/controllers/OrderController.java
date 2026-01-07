package com.shopsphere.api.controllers;

import com.shopsphere.api.domain.enums.OrderStatus;
import com.shopsphere.api.entity.Order;
import com.shopsphere.api.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
        }
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Retaining legacy path for backward compatibility if needed, or remove if fully switching.
    // Frontend uses query param so the above covers it.
    
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        // Validation: ensure id matches order.getId() if strictly required, 
        // or just pass to service. 
        // Assuming service handles logic or we just delegate.
        // For simplicity reusing updateOrderStatus logic or creating new service method for full update if needed.
        // But wait, OrderService currently only has 'updateOrderStatus'.
        // Frontend expects full update. We should probably add 'updateOrder' to Service too.
        // Let's assume we will add it to Service in a moment.
        return ResponseEntity.ok(orderService.updateOrder(id, order));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }
}
