package com.noonoo.prjtbackend.product.mapper;

import com.noonoo.prjtbackend.product.dto.ProductDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper {
    List<ProductDto> findProducts();
}
