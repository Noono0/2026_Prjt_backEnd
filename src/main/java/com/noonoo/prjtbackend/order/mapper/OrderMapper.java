package com.noonoo.prjtbackend.order.mapper;

import com.noonoo.prjtbackend.order.dto.OrderDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    List<OrderDto> findOrders();
}
