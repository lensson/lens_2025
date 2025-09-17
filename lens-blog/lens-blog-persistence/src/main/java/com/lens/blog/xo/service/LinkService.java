package com.lens.blog.xo.service;




import com.lens.blog.entity.Link;
import com.lens.blog.vo.LinkVO;
import com.lens.blog.xo.dto.LinkPageDTO;
import com.lens.common.db.mybatis.page.vo.PageVO;
import com.lens.common.db.mybatis.service.SuperService;

import java.util.List;

/**
 * 标签表 服务类
 *
 * @author 陌溪
 * @date 2018-09-08
 */
public interface LinkService extends SuperService<Link> {

    /**
     * 通过页大小获取友链列表
     *
     * @param pageSize
     * @return
     */
    public List<Link> getListByPageSize(Integer pageSize);

    /**
     * 获取友链列表
     *
     * @param pageDTO
     * @return
     */
    public PageVO<Link> getPageList(LinkPageDTO pageDTO);

    /**
     * 新增友链
     *
     * @param linkVO
     */
    public String addLink(LinkVO linkVO);

    /**
     * 编辑友链
     *
     * @param linkVO
     */
    public String editLink(LinkVO linkVO);

    /**
     * 删除友链
     *
     * @param linkVO
     */
    public String deleteLink(LinkVO linkVO);

    /**
     * 置顶友链
     *
     * @param linkVO
     */
    public String stickLink(LinkVO linkVO);

    /**
     * 点击友链
     *
     * @return
     */
    public String addLinkCount(String uid);
}
