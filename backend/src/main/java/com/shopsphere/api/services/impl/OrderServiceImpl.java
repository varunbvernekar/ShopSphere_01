package com.shopsphere.api.services.impl;

import com.shopsphere.api.enums.OrderStatus;
import com.shopsphere.api.dto.requestDTO.OrderRequest;
import com.shopsphere.api.dto.responseDTO.OrderResponse;
import com.shopsphere.api.entity.Order;
import com.shopsphere.api.repositories.OrderRepository;
import com.shopsphere.api.services.InventoryService;
import com.shopsphere.api.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final com.shopsphere.api.repositories.UserRepository userRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = OrderRequest.toEntity(orderRequest);

        // Validate and update stock
        if (order.getItems() != null) {
            for (var item : order.getItems()) {
                if (item.getProductId() != null) {
                    inventoryService.reduceStock(item.getProductId(), item.getQuantity());
                }
            }
        }
        Order savedOrder = orderRepository.save(order);
        return OrderResponse.fromEntity(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderResponse::fromEntity);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Security Check
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            String email = auth.getName();
            com.shopsphere.api.entity.User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!order.getUserId().equals(user.getId())) {
                throw new RuntimeException("Access Denied: You do not own this order.");
            }

            if (status != OrderStatus.Cancelled) {
                throw new RuntimeException("Customers can only cancel orders.");
            }
        }

        if (status == OrderStatus.Cancelled) {
            if (order.getStatus() == OrderStatus.Shipped || order.getStatus() == OrderStatus.Delivered) {
                throw new RuntimeException("Cannot cancel order that has already been shipped or delivered");
            }
            // Ideally we should restore stock here if cancelled
        }

        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        return OrderResponse.fromEntity(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(Long id, OrderRequest orderRequest) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        existing.setStatus(orderRequest.getStatus());
        existing.setDeliveryAddress(orderRequest.getDeliveryAddress());
        existing.setEstimatedDelivery(orderRequest.getEstimatedDelivery());

        if (orderRequest.getItems() != null) {
            Order tempOrder = OrderRequest.toEntity(orderRequest);
            existing.getItems().clear();
            existing.getItems().addAll(tempOrder.getItems());
        }

        if (orderRequest.getLogistics() != null) {
            if (existing.getLogistics() == null) {
                existing.setLogistics(new com.shopsphere.api.entity.LogisticsInfo());
            }
            existing.getLogistics().setCarrier(orderRequest.getLogistics().getCarrier());
            existing.getLogistics().setTrackingId(orderRequest.getLogistics().getTrackingId());
            existing.getLogistics().setCurrentLocation(orderRequest.getLogistics().getCurrentLocation());
        }

        Order savedOrder = orderRepository.save(existing);
        return OrderResponse.fromEntity(savedOrder);
    }
}
