package com.noonoo.prjtbackend.order.mapper;

import com.noonoo.prjtbackend.order.dto.OrderDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    List<OrderDto> findOrders();
}
