package com.noonoo.prjtbackend.order.serviceImpl;

import com.noonoo.prjtbackend.order.dto.OrderDto;
import com.noonoo.prjtbackend.order.mapper.OrderMapper;
import com.noonoo.prjtbackend.order.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    @Override
    public List<OrderDto> findOrders() {
        return orderMapper.findOrders();
    }
}
