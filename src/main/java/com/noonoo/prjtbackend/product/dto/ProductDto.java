package com.noonoo.prjtbackend.product.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Long categoryId;
}
