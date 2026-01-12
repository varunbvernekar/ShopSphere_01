package com.shopsphere.api.dto.requestDTO;

import lombok.Data;

@Data
public class StockUpdateRequest {
    private Integer quantity;
    private Integer threshold; // Nullable if only updating quantity
}
