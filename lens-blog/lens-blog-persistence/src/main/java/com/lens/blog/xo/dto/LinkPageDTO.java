package com.lens.blog.xo.dto;

import com.lens.blog.xo.constant.SQLConstants;

import com.lens.common.db.mybatis.page.dto.PageDTO;
import com.lens.common.db.mybatis.plugin.annotation.Query;
import com.lens.common.db.mybatis.plugin.enums.QueryWay;
import lombok.Data;

/**
 * @Author zhenac
 * @Created 5/30/25 2:14 PM
 */

@Data
public class LinkPageDTO extends PageDTO {

    /**
     * 友链标题
     */
    @Query(value = QueryWay.LIKE, fieldName = SQLConstants.TITLE)
    private String keyword;

    /**
     * 友链状态： 0 申请中， 1：已上线，  2：已拒绝
     */
    private Integer linkStatus;

}