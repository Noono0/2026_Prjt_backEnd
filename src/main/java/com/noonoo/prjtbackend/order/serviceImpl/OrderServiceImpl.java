package com.noonoo.prjtbackend.order.serviceImpl;

import com.noonoo.prjtbackend.order.dto.OrderDto;
import com.noonoo.prjtbackend.order.mapper.OrderMapper;
import com.noonoo.prjtbackend.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    @Override
    public List<OrderDto> findOrders() {
        return orderMapper.findOrders();
    }
}
