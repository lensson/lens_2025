package com.lens.blog.xo.dto;


import com.lens.common.db.mybatis.plugin.annotation.Query;
import com.lens.common.db.mybatis.plugin.enums.QueryWay;
import lombok.Data;
import lombok.Value;

/**
 * 角色分页
 *
 * @author geshanzsq
 * @date 2024/6/16
 */
@Data
public class RolePageDTO {

    /**
     * 角色名称
     */
    @Query(value = QueryWay.LIKE, fieldName = "roleName")
    private String keyword;

    /**
     * 角色状态
     */
    private Integer status;

}
