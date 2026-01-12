package com.shopsphere.api.services;

import com.shopsphere.api.enums.OrderStatus;
import com.shopsphere.api.dto.requestDTO.OrderRequest;
import com.shopsphere.api.dto.responseDTO.OrderResponse;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);

    List<OrderResponse> getOrdersByUserId(Long userId);

    List<OrderResponse> getAllOrders();

    Optional<OrderResponse> getOrderById(Long id);

    OrderResponse updateOrderStatus(Long id, OrderStatus status);

    OrderResponse updateOrder(Long id, OrderRequest orderRequest);
}
