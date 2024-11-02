package io.github.cmmplb.activiti.configuration;

import io.github.cmmplb.activiti.configuration.properties.ActivitiProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author penglibo
 * @date 2024-11-01 15:40:52
 * @since jdk 1.8
 */

@Configuration
@EnableConfigurationProperties(ActivitiProperties.class)
public class ActivitiConfiguration {
}
