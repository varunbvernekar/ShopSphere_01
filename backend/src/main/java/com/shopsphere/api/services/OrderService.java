package com.shopsphere.api.services;

import com.shopsphere.api.domain.enums.OrderStatus;
import com.shopsphere.api.entity.Order;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);
    List<Order> getOrdersByUserId(Long userId);
    List<Order> getAllOrders();
    Optional<Order> getOrderById(Long id);
    Order updateOrderStatus(Long id, OrderStatus status);
    Order updateOrder(Long id, Order order);
}
