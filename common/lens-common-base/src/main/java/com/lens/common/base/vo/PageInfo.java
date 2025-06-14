package com.lens.common.base.vo;


import com.lens.common.base.validator.Messages;
import com.lens.common.base.validator.annotion.LongNotNull;
import com.lens.common.base.validator.group.GetList;
import lombok.Data;

/**
 * PageVO  用于分页
 *
 * @author: 陌溪
 * @create: 2019-12-03-22:38
 */
@Data
public class PageInfo<T> {

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 当前页
     */
    @LongNotNull(groups = {GetList.class}, message = Messages.PAGE_NOT_NULL)
    private Long currentPage;

    /**
     * 页大小
     */
    @LongNotNull(groups = {GetList.class}, message = Messages.SIZE_NOT_NULL)
    private Long pageSize;
}
