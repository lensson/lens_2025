package com.lens.blog.vo;

import com.lens.common.base.validator.annotion.NotBlank;
import com.lens.common.base.validator.group.Insert;
import com.lens.common.base.validator.group.Update;
import com.lens.common.base.vo.BaseVO;
import lombok.Data;

/**
 * BlogSortVO
 *
 * @author: 陌溪
 * @create: 2019年12月6日12:56:08
 */
@Data
public class BlogSortVO extends BaseVO<BlogSortVO> {

    /**
     * 分类名
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String sortName;

    /**
     * 分类介绍
     */
    private String content;

    /**
     * 排序字段
     */
    private Integer sort;


    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;

    /**
     * 无参构造方法
     */
    BlogSortVO() {

    }

}
