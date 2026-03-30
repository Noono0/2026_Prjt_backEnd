package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;

/** 수량 또는 횟수 (구매·교환 공통) */
@Getter
@Setter
public class WalletQuantityRequest {
    /** 1 이상 */
    private Integer quantity;
}
