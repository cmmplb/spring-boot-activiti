package io.github.cmmplb.activiti.convert;

import io.github.cmmplb.activiti.domain.vo.ProcessDefinitionVO;
import org.activiti.engine.repository.ProcessDefinition;
import org.mapstruct.Mapper;

/**
 * @author penglibo
 * @date 2022-08-03 16:56:25
 * @since jdk 1.8
 */

@Mapper
public interface ProcessDefinitionConvert extends Converter<ProcessDefinition, ProcessDefinitionVO> {

}