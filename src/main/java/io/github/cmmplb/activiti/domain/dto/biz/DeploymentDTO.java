package io.github.cmmplb.activiti.domain.dto.biz;

import lombok.Data;

/**
 * @author penglibo
 * @date 2024-11-10 13:23:33
 * @since jdk 1.8
 */

@Data
public class DeploymentDTO {

    private String key;

    private String name;

    private String category;

    private String description;
}
