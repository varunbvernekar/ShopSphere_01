package com.shopsphere.api.controllers;

import com.shopsphere.api.domain.enums.OrderStatus;
import com.shopsphere.api.entity.Order;
import com.shopsphere.api.entity.User;
import com.shopsphere.api.services.OrderService;
import com.shopsphere.api.services.UserService;
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
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@RequestParam(required = false) Long userId) {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            // Find the ID of the current authenticated user via email from context
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));
            return ResponseEntity.ok(orderService.getOrdersByUserId(user.getId()));
        }

        if (userId != null) {
            return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
        }
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Retaining legacy path for backward compatibility if needed, or remove if
    // fully switching.
    // Frontend uses query param so the above covers it.

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        // Validation: ensure id matches order.getId() if strictly required,
        // or just pass to service.
        // Assuming service handles logic or we just delegate.
        // For simplicity reusing updateOrderStatus logic or creating new service method
        // for full update if needed.
        // But wait, OrderService currently only has 'updateOrderStatus'.
        // Frontend expects full update. We should probably add 'updateOrder' to Service
        // too.
        // Let's assume we will add it to Service in a moment.
        return ResponseEntity.ok(orderService.updateOrder(id, order));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }
}
