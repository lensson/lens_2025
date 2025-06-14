package com.lens.blog.vo;


import com.lens.common.base.validator.annotion.NotBlank;
import com.lens.common.base.validator.group.GetList;
import com.lens.common.base.validator.group.GetOne;
import com.lens.common.base.validator.group.Insert;
import com.lens.common.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;

/**
 * CommentVO
 *
 * @author: 陌溪
 * @create: 2020年1月11日16:15:52
 */
@ToString
@Data
public class CommentVO extends BaseVO<CommentVO> {

    /**
     * 用户uid
     */
    @NotBlank(groups = {Insert.class, GetOne.class})
    private String userUid;

    /**
     * 回复某条评论的uid
     */
    private String toUid;

    /**
     * 回复某个人的uid
     */
    private String toUserUid;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 评论类型： 0: 评论   1: 点赞
     */
    private Integer type;

    /**
     * 评论内容
     */
    @NotBlank(groups = {Insert.class})
    private String content;

    /**
     * 博客uid
     */
    private String blogUid;

    /**
     * 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等
     */
    @NotBlank(groups = {Insert.class, GetList.class})
    private String source;
}
// CLB(CreateLocalBuild) Script  --  A python script to create a complete Altiplano blueprint extension super ZIP on local development environment with user-friendly command line.