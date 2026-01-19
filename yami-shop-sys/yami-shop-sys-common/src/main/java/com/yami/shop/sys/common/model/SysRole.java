/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.sys.common.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 角色
 * @author yami
 */
@Data
@TableName("tz_sys_role")
public class SysRole implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     *
     */
    @TableId
    @Schema(description = "角色ID" )
    private Long roleId;

    @NotBlank(message="角色名称不能为空")
    @Schema(description = "角色名称" )
    private String roleName;

    @Schema(description = "备注" )
    private String remark;

    @TableField(exist=false)
    @Schema(description = "菜单id列表" )
    private List<Long> menuIdList;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间" )
    private Date createTime;
}
