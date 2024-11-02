package io.github.cmmplb.activiti.convert;

import io.github.cmmplb.activiti.domain.vo.DeploymentVO;
import org.activiti.engine.repository.Deployment;
import org.mapstruct.Mapper;

/**
 * @author penglibo
 * @date 2022-08-03 16:56:25
 * @since jdk 1.8
 */

@Mapper
public interface DeploymentConvert extends Converter<Deployment, DeploymentVO> {

}