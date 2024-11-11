package io.github.cmmplb.activiti.domain.entity.biz;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author penglibo
 * @date 2024-11-08 10:48:05
 * @since jdk 1.8
 * 行政管理申请表细表
 */

@Data
@TableName(value = "`biz_administration_apply_detail`")
public class AdministrationApplyDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 行政管理申请表 id
     */
    @TableField(value = "`administration_apply_id`")
    private Long administrationApplyId;

    /**
     * 类型:入库类型:1-正常入库; 2-退货入库; 3-调拨入库; 出库类型:1-物品领用; 2-报损; 3-退库;
     */
    @TableField(value = "`type`")
    private Byte type;

    /**
     * 数量
     */
    @TableField(value = "`quantity`")
    private Integer quantity;

    /**
     * 领用物料名称, 这里演示就固定几个物料
     */
    @TableField(value = "`material_name`")
    private String materialName;

    /**
     * 创建时间
     */
    @TableField(value = "`create_time`")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "`update_time`")
    private Date updateTime;
}