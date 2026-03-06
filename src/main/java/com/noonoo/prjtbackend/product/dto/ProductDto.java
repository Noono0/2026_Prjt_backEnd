package com.noonoo.prjtbackend.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDto {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Long categoryId;
}
