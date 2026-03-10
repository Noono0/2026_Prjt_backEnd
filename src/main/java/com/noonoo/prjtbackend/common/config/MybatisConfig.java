package com.noonoo.prjtbackend.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.noonoo.prjtbackend.*.mapper")
public class MybatisConfig {
}
