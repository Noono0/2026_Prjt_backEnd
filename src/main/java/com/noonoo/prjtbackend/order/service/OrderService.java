package com.noonoo.prjtbackend.order.service;

import com.noonoo.prjtbackend.order.dto.OrderDto;

import java.util.List;

public interface OrderService {
    List<OrderDto> findOrders();
}
