package com.noonoo.prjtbackend.order.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.order.dto.OrderDto;
import com.noonoo.prjtbackend.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("@securityExpressions.isAuthenticatedOrPermitAll()")
    public ApiResponse<List<OrderDto>> findOrders() {
        return ApiResponse.ok(orderService.findOrders());
    }
}
