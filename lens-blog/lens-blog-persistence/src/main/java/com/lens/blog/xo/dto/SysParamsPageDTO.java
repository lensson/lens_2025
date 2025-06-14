package com.lens.blog.xo.dto;


import com.lens.common.db.mybatis.page.dto.PageDTO;
import com.lens.common.db.mybatis.plugin.annotation.Query;
import com.lens.common.db.mybatis.plugin.enums.QueryWay;
import lombok.Data;

/**
 * @author geshanzsq
 * @date 2024/6/3
 */
@Data
public class SysParamsPageDTO extends PageDTO {

    /**
     * 参数名称
     */
    @Query(QueryWay.LIKE)
    private String paramsName;

    /**
     * 参数键名
     */
    @Query(QueryWay.LIKE)
    private String paramsKey;
}
