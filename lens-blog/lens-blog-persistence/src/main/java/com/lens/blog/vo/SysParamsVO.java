package com.lens.blog.vo;

import com.lens.common.base.validator.annotion.IntegerNotNull;
import com.lens.common.base.validator.annotion.NotBlank;
import com.lens.common.base.validator.group.Insert;
import com.lens.common.base.validator.group.Update;
import com.lens.common.base.vo.BaseVO;
import lombok.Data;

/**
 * TodoVO
 *
 * @author: 陌溪
 * @create: 2019年12月18日22:16:23
 */
@Data
public class SysParamsVO extends BaseVO<SysParamsVO> {


    /**
     * 参数名称
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String paramsName;

    /**
     * 参数键名
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String paramsKey;

    /**
     * 参数键值
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String paramsValue;

    /**
     * 参数类型，是否系统内置（1：是，0：否）
     */
    @IntegerNotNull(groups = {Insert.class, Update.class})
    private Integer paramsType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序字段
     */
    @IntegerNotNull(groups = {Insert.class, Update.class})
    private Integer sort;

}
