package io.github.cmmplb.activiti.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author penglibo
 * @date 2024-10-22 14:50:32
 * @since jdk 1.8
 * 声明式事务配置
 */

@Configuration
@ImportResource("classpath*:tx/transaction.xml")
public class TxConfiguration {
}