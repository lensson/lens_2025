package com.lens.blog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lens.blog.entity.Link;
import com.lens.blog.xo.mapper.LinkMapper;
import com.lens.blog.vo.LinkVO;
import com.lens.blog.xo.constant.MessageConstants;
import com.lens.blog.xo.constant.RedisConstants;
import com.lens.blog.xo.constant.SQLConstants;
import com.lens.blog.xo.constant.SysConstants;
import com.lens.blog.xo.dto.LinkPageDTO;
import com.lens.blog.xo.service.LinkService;
import com.lens.blog.xo.utils.RabbitMqUtil;
import com.lens.blog.xo.utils.WebUtil;
import com.lens.common.base.constant.Constants;
import com.lens.common.base.enums.ELinkStatus;
import com.lens.common.base.enums.EStatus;
import com.lens.common.core.utils.CheckUtils;
import com.lens.common.core.utils.ResultUtil;
import com.lens.common.core.utils.StringUtils;
import com.lens.common.db.constant.BaseSQLConstants;
import com.lens.common.db.mybatis.page.vo.PageVO;
import com.lens.common.db.mybatis.serviceImpl.SuperServiceImpl;
import com.lens.common.redis.utils.RedisUtil;
import com.lens.common.web.feign.PictureFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 友链表 服务实现类
 *
 * @author 陌溪
 * @date 2018-09-08
 */
@Service
@Slf4j
public class LinkServiceImpl extends SuperServiceImpl<LinkMapper, Link> implements LinkService {

    @Resource
    private LinkMapper linkMapper;
    @Autowired
    private LinkService linkService;
    @Resource
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private WebUtil webUtil;
    @Autowired
    private RabbitMqUtil rabbitMqUtil;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<Link> getListByPageSize(Integer pageSize) {
        QueryWrapper<Link> queryWrapper = new QueryWrapper<>();
        Page<Link> page = new Page<>();
        page.setCurrent(1);
        page.setSize(pageSize);
        queryWrapper.eq(BaseSQLConstants.LINK_STATUS, ELinkStatus.PUBLISH);
        queryWrapper.eq(BaseSQLConstants.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(BaseSQLConstants.SORT);
        IPage<Link> pageList = linkMapper.selectPage(page, queryWrapper);
        return pageList.getRecords();
    }

    @Override
    public PageVO<Link> getPageList(LinkPageDTO pageDTO) {
        if (StringUtils.isBlank(pageDTO.getOrderByDescColumn()) && StringUtils.isBlank(pageDTO.getOrderByAscColumn())) {
            pageDTO.setOrderByDescColumn(SQLConstants.SORT);
        }
        PageVO<Link> pageVO = linkMapper.selectPage(pageDTO);
        List<Link> linkList = pageVO.getRecords();
        if (CollectionUtils.isEmpty(linkList)) {
            return pageVO;
        }

        final StringBuffer fileUids = new StringBuffer();
        // 给友情链接添加图片
        linkList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUids.append(item.getFileUid() + SysConstants.FILE_SEGMENTATION);
            }
        });
        String pictureList = null;
        Map<String, String> pictureMap = new HashMap<>();
        if (fileUids != null) {
            pictureList = pictureFeignClient.getPicture(fileUids.toString(), SysConstants.FILE_SEGMENTATION);
        }
        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureList);
        picList.forEach(item -> {
            pictureMap.put(item.get(SysConstants.UID).toString(), item.get(SysConstants.URL).toString());
        });
        for (Link item : linkList) {
            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getFileUid(), Constants.SYMBOL_COMMA);
                List<String> pictureListTemp = new ArrayList<>();

                pictureUidsTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                item.setPhotoList(pictureListTemp);
            }
        }
        return pageVO;
    }

    @Override
    public String addLink(LinkVO linkVO) {
        Link link = new Link();
        link.setTitle(linkVO.getTitle());
        link.setSummary(linkVO.getSummary());
        link.setUrl(linkVO.getUrl());
        link.setClickCount(0);
        link.setLinkStatus(linkVO.getLinkStatus());
        link.setSort(linkVO.getSort());
        link.setEmail(linkVO.getEmail());
        link.setFileUid(linkVO.getFileUid());
        link.setStatus(EStatus.ENABLE);
        link.setUpdateTime(new Date());
        link.insert();

        // 友链从申请状态到发布状态，需要发送邮件到站长邮箱
        if(StringUtils.isNotEmpty(link.getEmail()) && CheckUtils.checkEmail(link.getEmail())) {
            log.info("发送友链申请通过的邮件通知");
            String linkApplyText =  "<a href=\" " + link.getUrl() + "\">" + link.getTitle() + "</a> 站长，您申请的友链已经成功上架~";
            rabbitMqUtil.sendSimpleEmail(link.getEmail(), linkApplyText);
        }

        // 删除Redis中的BLOG_LINK
        deleteRedisBlogLinkList();

        return ResultUtil.successWithMessage(MessageConstants.INSERT_SUCCESS);
    }

    @Override
    public String editLink(LinkVO linkVO) {
        Link link = linkService.getById(linkVO.getUid());
        Integer linkStatus = link.getLinkStatus();
        link.setTitle(linkVO.getTitle());
        link.setSummary(linkVO.getSummary());
        link.setLinkStatus(linkVO.getLinkStatus());
        link.setUrl(linkVO.getUrl());
        link.setSort(linkVO.getSort());
        link.setEmail(linkVO.getEmail());
        link.setFileUid(linkVO.getFileUid());
        link.setUpdateTime(new Date());
        link.updateById();

        // 友链从申请状态到发布状态，需要发送邮件到站长邮箱
        if(StringUtils.isNotEmpty(link.getEmail()) && CheckUtils.checkEmail(link.getEmail())) {
            if(ELinkStatus.APPLY.equals(linkStatus) && ELinkStatus.PUBLISH.equals(linkVO.getLinkStatus())) {
                log.info("发送友链申请通过的邮件通知");
                String linkApplyText =  "<a href=\" " + link.getUrl() + "\">" + link.getTitle() + "</a> 站长，您申请的友链已经成功上架~";
                rabbitMqUtil.sendSimpleEmail(link.getEmail(), linkApplyText);
            }
        }

        // 删除Redis中的BLOG_LINK
        deleteRedisBlogLinkList();

        return ResultUtil.successWithMessage(MessageConstants.UPDATE_SUCCESS);
    }

    @Override
    public String deleteLink(LinkVO linkVO) {
        Link link = linkService.getById(linkVO.getUid());
        link.setStatus(EStatus.DISABLED);
        link.setUpdateTime(new Date());
        link.updateById();

        // 删除Redis中的BLOG_LINK
        deleteRedisBlogLinkList();

        return ResultUtil.successWithMessage(MessageConstants.DELETE_SUCCESS);
    }

    @Override
    public String stickLink(LinkVO linkVO) {
        Link link = linkService.getById(linkVO.getUid());
        //查找出最大的那一个
        QueryWrapper<Link> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(SQLConstants.SORT);
        Page<Link> page = new Page<>();
        page.setCurrent(0);
        page.setSize(1);
        IPage<Link> pageList = linkService.page(page, queryWrapper);
        List<Link> list = pageList.getRecords();
        Link maxSort = list.get(0);
        if (StringUtils.isEmpty(maxSort.getUid())) {
            return ResultUtil.errorWithMessage(MessageConstants.PARAM_INCORRECT);
        }
        if (maxSort.getUid().equals(link.getUid())) {
            return ResultUtil.errorWithMessage(MessageConstants.OPERATION_FAIL);
        }
        Integer sortCount = maxSort.getSort() + 1;
        link.setSort(sortCount);
        link.setUpdateTime(new Date());
        link.updateById();
        // 删除Redis中的BLOG_LINK
        deleteRedisBlogLinkList();
        return ResultUtil.successWithMessage(MessageConstants.OPERATION_SUCCESS);
    }

    @Override
    public String addLinkCount(String uid) {
        if (StringUtils.isEmpty(uid)) {
            return ResultUtil.errorWithMessage(MessageConstants.PARAM_INCORRECT);
        }
        Link link = linkService.getById(uid);
        if (link != null) {
            int count = link.getClickCount() + 1;
            link.setClickCount(count);
            link.updateById();
        } else {
            return ResultUtil.errorWithMessage(MessageConstants.PARAM_INCORRECT);
        }
        return ResultUtil.successWithMessage(MessageConstants.UPDATE_SUCCESS);
    }

    /**
     * 删除Redis中的友链列表
     */
    private void deleteRedisBlogLinkList() {
        // 删除Redis中的BLOG_LINK
        Set<String> keys = redisUtil.keys(RedisConstants.BLOG_LINK + Constants.SYMBOL_COLON + "*");
        redisUtil.delete(keys);
    }
}
