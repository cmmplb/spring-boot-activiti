package io.github.cmmplb.activiti.dao.biz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.cmmplb.activiti.domain.dto.biz.ApplyDTO;
import io.github.cmmplb.activiti.domain.dto.biz.ApplyQueryDTO;
import io.github.cmmplb.activiti.domain.entity.biz.Apply;
import io.github.cmmplb.activiti.domain.vo.biz.ApplyDetailsVO;
import io.github.cmmplb.activiti.domain.vo.biz.ApplyVO;
import org.apache.ibatis.annotations.Param;

/**
* @author penglibo
* @date 2024-11-08 10:48:05
* @since jdk 1.8
*/
public interface ApplyMapper extends BaseMapper<Apply> {

    Page<ApplyVO> selectByPaged(@Param("page") Page<ApplyVO> page, @Param("dto") ApplyQueryDTO dto);

    ApplyDetailsVO selectDetailsById(@Param("id") Long id);
}