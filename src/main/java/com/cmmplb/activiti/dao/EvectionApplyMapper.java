package com.cmmplb.activiti.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cmmplb.activiti.entity.EvectionApply;
import com.cmmplb.activiti.vo.EvectionApplyDetailsVO;
import org.apache.ibatis.annotations.Param;

/**
 * @author penglibo
 * @date 2021-04-02 00:03:34
 * MP 支持不需要 Mapper.xml
 */

public interface EvectionApplyMapper extends BaseMapper<EvectionApply> {

    EvectionApplyDetailsVO selectDetailsById(@Param("id") Long id);
}
