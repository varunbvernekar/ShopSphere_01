package com.shopsphere.api.services;

import com.shopsphere.api.domain.enums.OrderStatus;
import com.shopsphere.api.entity.Order;
import com.shopsphere.api.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Override
    @Transactional
    public Order createOrder(Order order) {
        // Validate and update stock
        if (order.getItems() != null) {
            for (var item : order.getItems()) {
                if (item.getProductId() != null) {
                    String productId = item.getProductId();
                    // Fetch product to check stock first
                    var productOpt = productService.getProductById(productId);
                    if (productOpt.isPresent()) {
                        var product = productOpt.get();
                        int newStock = product.getStockLevel() - item.getQuantity();
                        if (newStock < 0) {
                            throw new RuntimeException("Insufficient stock for product: " + product.getName());
                        }
                        productService.updateStock(productId, newStock);
                    } else {
                        throw new RuntimeException("Product not found: " + productId);
                    }
                }
            }
        }
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (status == OrderStatus.Cancelled) {
            if (order.getStatus() == OrderStatus.Shipped || order.getStatus() == OrderStatus.Delivered) {
                throw new RuntimeException("Cannot cancel order that has already been shipped or delivered");
            }
        }

        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, Order updatedOrder) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        // Update fields that are allowed to be updated.
        // For a full PUT, generally we might replace most things,
        // but let's be safe and update specific logistics/status or info.
        // Frontend might send everything.
        existing.setStatus(updatedOrder.getStatus());
        existing.setDeliveryAddress(updatedOrder.getDeliveryAddress());
        existing.setLogistics(updatedOrder.getLogistics());
        existing.setEstimatedDelivery(updatedOrder.getEstimatedDelivery());

        // If we want to allow updating items, it's more complex (delete orphans etc).
        // For now, assuming items are not updated via this endpoint usually,
        // but if they are, we'd need to handle that list carefully.
        // Given 'Customize Product' and 'Order' context, usually order items are fixed
        // after placement.
        // But let's allow updating them if provided:
        if (updatedOrder.getItems() != null) {
            existing.getItems().clear();
            existing.getItems().addAll(updatedOrder.getItems());
        }

        return orderRepository.save(existing);
    }
}
