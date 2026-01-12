package com.shopsphere.api.dto.responseDTO;

import com.shopsphere.api.enums.OrderStatus;
import com.shopsphere.api.entity.Address;
import com.shopsphere.api.entity.Order;
import com.shopsphere.api.entity.OrderItem;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private LocalDateTime placedOn;
    private Double amount;
    private OrderStatus status;
    private List<OrderItemResponse> items;
    private String estimatedDelivery;
    private LogisticsResponse logistics;
    private Address deliveryAddress;

    public static OrderResponse fromEntity(Order order) {
        if (order == null) {
            return null;
        }

        LogisticsResponse logisticsDTO = null;
        if (order.getLogistics() != null) {
            logisticsDTO = LogisticsResponse.builder()
                    .carrier(order.getLogistics().getCarrier())
                    .trackingId(order.getLogistics().getTrackingId())
                    .currentLocation(order.getLogistics().getCurrentLocation())
                    .build();
        }

        List<OrderItemResponse> items = Collections.emptyList();
        if (order.getItems() != null) {
            items = order.getItems().stream()
                    .map(OrderResponse::toItemResponse)
                    .collect(Collectors.toList());
        }

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .placedOn(order.getPlacedOn())
                .amount(order.getAmount())
                .status(order.getStatus())
                .items(items)
                .estimatedDelivery(order.getEstimatedDelivery())
                .logistics(logisticsDTO)
                .deliveryAddress(order.getDeliveryAddress())
                .build();
    }

    private static OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .name(item.getName())
                .image(item.getImage())
                .quantity(item.getQuantity())
                .color(item.getColor())
                .size(item.getSize())
                .material(item.getMaterial())
                .price(item.getPrice())
                .build();
    }
}
