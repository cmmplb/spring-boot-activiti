package com.cmmplb.activiti.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author plb
 * @date 2019/12/29
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /**
     * 总条数
     */
    private Long total;

    /**
     * 分页数据
     */
    private List<T> rows;
}
