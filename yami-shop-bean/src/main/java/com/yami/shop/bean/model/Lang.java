package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author letere
 * @since 2024-03-25
 */
@Getter
@Setter
@TableName("tz_lang")
public class Lang {

    /**
     * 主键
     */
    @TableId(value = "lang_id", type = IdType.AUTO)
    private Integer langId;

    /**
     * 语言名称
     */
    private String name;

    /**
     * 语言编号
     */
    private Integer lang;

    /**
     * 语言代码
     */
    private String language;

    /**
     * 是否默认
     */
    private Integer master;

    /**
     * 是否隐藏
     */
    private Integer hide;

    /**
     * 备注
     */
    private String remark;
}
