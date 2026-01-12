package com.shopsphere.api.dto.responseDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogisticsResponse {
    private String carrier;
    private String trackingId;
    private String currentLocation;
}
