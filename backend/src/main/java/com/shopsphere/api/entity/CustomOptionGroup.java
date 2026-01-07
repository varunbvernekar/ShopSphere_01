package com.shopsphere.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "custom_option_groups")
public class CustomOptionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    @ElementCollection
    @CollectionTable(name = "custom_option_values", joinColumns = @JoinColumn(name = "option_group_id"))
    @Column(name = "value_name")
    private List<String> values;

    @ElementCollection
    @CollectionTable(name = "custom_option_price_adjustments", joinColumns = @JoinColumn(name = "option_group_id"))
    @MapKeyColumn(name = "option_value")
    @Column(name = "adjustment_amount")
    private Map<String, Double> priceAdjustment;
}
