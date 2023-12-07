package com.cmmplb.activiti.beans;

import lombok.Data;

import java.io.Serializable;

/**
 * @author plb
 * @date 2020/6/15 11:46
 */

@Data
public class QueryPageBean implements Serializable {

    private static final long serialVersionUID = -6159534153273079325L;

    /**
     * 每页条数
     */
    private int size;

    /**
     * 当前页
     */
    private int current;

    /**
     * 起始
     */
    private int start;

    /**
     * 查询条件
     */
    private String keywords;

    public int getCurrent() {
        return (0 == current) ? 1 : current;
    }

    public int getSize() {
        return size == 0 ? 10 : size;
    }

    public int getStart() {
        if (0 == current) {
            return start;
        } else {
            return (current <= 1) ? 0 : ((current - 1) * size);
        }
    }
}
