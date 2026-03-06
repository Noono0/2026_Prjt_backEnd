package com.noonoo.prjtbackend.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "order_status")
    private String orderStatus;
}
