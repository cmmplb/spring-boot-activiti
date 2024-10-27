package io.github.cmmplb.activiti.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author plb
 * @date 2020/6/15 11:46
 */

@Data
@ApiModel(value = "QueryPageBean", description = "分页条件请求参数")
public class QueryPageBean implements Serializable {

    private static final long serialVersionUID = -6159534153273079325L;

    @ApiModelProperty(value = "每页条数", example = "10")
    private int size;

    @ApiModelProperty(value = "当前页", example = "1")
    private int current;

    @ApiModelProperty(value = "起始页, 自动计算", hidden = true)
    private int start;

    @ApiModelProperty(value = "关键词")
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
