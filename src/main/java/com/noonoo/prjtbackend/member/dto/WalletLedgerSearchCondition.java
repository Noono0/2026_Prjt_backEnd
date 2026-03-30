package com.noonoo.prjtbackend.member.dto;

import com.noonoo.prjtbackend.common.paging.PageRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletLedgerSearchCondition extends PageRequest {
    private Long memberSeq;
}
