package com.noonoo.prjtbackend.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
    private Long orderId;
    private Long memberId;
    private String orderStatus;
}
