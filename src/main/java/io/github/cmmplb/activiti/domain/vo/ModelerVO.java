package io.github.cmmplb.activiti.domain.vo;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author penglibo
 * @date 2023-10-17 11:13:43
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "ModelerVO", description = "流程设计返回参数")
public class ModelerVO {

    @ApiModelProperty(value = "设计页面中使用的字段为 modelId", example = "1")
    private String modelId;

    @ApiModelProperty(value = "名称", example = "请假模型")
    private String name;

    @ApiModelProperty(value = "创建时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "模型描述", example = "请假申请流程")
    private String description;

    @ApiModelProperty(value = "以json格式保存流程定义的信息,数据源信息", example = "{\"name\":\"请假申请\",\"description\":\"请假申请流程\",\"revision\":1}")
    private String metaInfo;

    @ApiModelProperty(value = "流程设计 json 信息")
    private JSONObject model;

    // 以下两个字段对应 /editor-app/stencil-controller.js
    // if (canvasSelected) {
    //     selectedItem.auditData = {
    //         'author': $scope.modelData.createdByUser,
    //         'createDate': $scope.modelData.createDate
    //     };
    // }
    @ApiModelProperty(value = "流程设计页面创建人字段")
    private String createdByUser;

    @ApiModelProperty(value = "流程设计页面创建时间字段")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    // 重写方法
    public String getDescription() {
        if (StringUtils.isNotEmpty(metaInfo)) {
            // 获取 metaInfo 中的 description 信息
            return JSONObject.parseObject(metaInfo).getString("description");
        }
        return description;
    }

    public Date getCreateDate() {
        return createTime;
    }

    public String getCreatedByUser() {
        // 后续整合 SpringSecurity
        return "管理员";
    }
}
