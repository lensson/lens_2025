package com.lens.blog.vo;


import com.lens.common.base.validator.annotion.NotBlank;
import com.lens.common.base.validator.group.Insert;
import com.lens.common.base.validator.group.Update;
import com.lens.common.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;

/**
 * StudyVideoVO
 *
 * @author: 陌溪
 * @create: 2020年1月10日22:30:29
 */
@ToString
@Data
public class StudyVideoVO extends BaseVO<StudyVideoVO> {

    /**
     * 视频名称
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String name;

    /**
     * 视频简介
     */
    private String summary;

    /**
     * 视频内容介绍
     */
    private String content;

    /**
     * 百度云完整路径
     */
    private String baiduPath;

    /**
     * 视频封面图片UID
     */
    private String fileUid;

    /**
     * 资源分类UID
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String resourceSortUid;

    /**
     * 无参构造方法，初始化默认值
     */
    StudyVideoVO() {

    }

}
