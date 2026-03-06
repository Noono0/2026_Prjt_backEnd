package com.noonoo.prjtbackend.product.service;

import com.noonoo.prjtbackend.product.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> findProducts();
}
