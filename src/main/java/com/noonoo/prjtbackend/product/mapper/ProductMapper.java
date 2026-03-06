package com.noonoo.prjtbackend.product.mapper;

import com.noonoo.prjtbackend.product.dto.ProductDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {
    List<ProductDto> findProducts();
}
