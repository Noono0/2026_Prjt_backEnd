package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberWalletBalance {
    private Long memberSeq;
    private Long pointBalance;
    private Integer ironQty;
    private Integer silverQty;
    private Integer goldQty;
    private Integer diamondQty;
}
