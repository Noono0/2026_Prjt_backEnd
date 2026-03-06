package com.noonoo.prjtbackend.product.serviceImpl;

import com.noonoo.prjtbackend.product.dto.ProductDto;
import com.noonoo.prjtbackend.product.mapper.ProductMapper;
import com.noonoo.prjtbackend.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public List<ProductDto> findProducts() {
        return productMapper.findProducts();
    }
}
