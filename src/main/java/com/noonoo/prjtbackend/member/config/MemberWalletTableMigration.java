package com.noonoo.prjtbackend.member.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(3)
@RequiredArgsConstructor
public class MemberWalletTableMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute(
                    """
                            CREATE TABLE IF NOT EXISTS member_wallet (
                                member_seq BIGINT NOT NULL PRIMARY KEY,
                                point_balance BIGINT NOT NULL DEFAULT 0,
                                iron_qty INT NOT NULL DEFAULT 0,
                                silver_qty INT NOT NULL DEFAULT 0,
                                gold_qty INT NOT NULL DEFAULT 0,
                                diamond_qty INT NOT NULL DEFAULT 0,
                                modify_dt DATETIME NULL,
                                CONSTRAINT fk_mw_member FOREIGN KEY (member_seq) REFERENCES member (member_seq) ON DELETE CASCADE
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                            """);
            jdbcTemplate.execute(
                    """
                            CREATE TABLE IF NOT EXISTS member_wallet_ledger (
                                ledger_seq BIGINT AUTO_INCREMENT PRIMARY KEY,
                                member_seq BIGINT NOT NULL,
                                reason_code VARCHAR(40) NOT NULL,
                                summary VARCHAR(500) NULL,
                                point_delta BIGINT NOT NULL DEFAULT 0,
                                iron_delta INT NOT NULL DEFAULT 0,
                                silver_delta INT NOT NULL DEFAULT 0,
                                gold_delta INT NOT NULL DEFAULT 0,
                                diamond_delta INT NOT NULL DEFAULT 0,
                                create_dt DATETIME NOT NULL,
                                create_id VARCHAR(50) NULL,
                                KEY idx_mwl_member_dt (member_seq, ledger_seq),
                                CONSTRAINT fk_mwl_member FOREIGN KEY (member_seq) REFERENCES member (member_seq) ON DELETE CASCADE
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                            """);
            log.info("DB: member_wallet / member_wallet_ledger 확인·생성");
        } catch (Exception e) {
            log.warn("member_wallet 테이블 자동 생성 실패: {}", e.toString());
        }
    }
}
