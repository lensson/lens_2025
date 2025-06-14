package com.lens.blog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lens.blog.xo.constant.MessageConstants;
import com.lens.blog.xo.constant.SQLConstants;
import com.lens.blog.mapper.SubjectItemMapper;
import com.lens.blog.xo.service.BlogService;
import com.lens.blog.xo.service.SubjectItemService;
import com.lens.blog.vo.SubjectItemVO;
import com.lens.common.base.constant.ErrorCode;
import com.lens.common.base.enums.EStatus;
import com.lens.common.base.exception.exceptionType.DeleteException;
import com.lens.common.core.utils.ResultUtil;
import com.lens.common.core.utils.StringUtils;
import com.lens.common.db.constant.BaseSQLConstants;
import com.lens.common.db.entity.Blog;
import com.lens.common.db.entity.SubjectItem;
import com.lens.common.db.mybatis.serviceImpl.SuperServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 专题item 服务实现类
 * </p>
 *
 * @author 陌溪
 * @since 2020年8月23日08:08:18
 */
@Service
public class SubjectItemServiceImpl extends SuperServiceImpl<SubjectItemMapper, SubjectItem> implements SubjectItemService {

    @Resource
    SubjectItemService subjectItemService;
    @Resource
    BlogService blogService;

    @Override
    public IPage<SubjectItem> getPageList(SubjectItemVO subjectItemVO) {
        QueryWrapper<SubjectItem> queryWrapper = new QueryWrapper<>();
        Page<SubjectItem> page = new Page<>();
        if (StringUtils.isNotEmpty(subjectItemVO.getSubjectUid())) {
            queryWrapper.eq(BaseSQLConstants.SUBJECT_UID, subjectItemVO.getSubjectUid());
        }
        page.setCurrent(subjectItemVO.getCurrentPage());
        page.setSize(subjectItemVO.getPageSize());
        queryWrapper.eq(BaseSQLConstants.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(BaseSQLConstants.SORT);
        IPage<SubjectItem> pageList = subjectItemService.page(page, queryWrapper);
        List<SubjectItem> subjectItemList = pageList.getRecords();
        List<String> blogUidList = new ArrayList<>();
        subjectItemList.forEach(item -> {
            blogUidList.add(item.getBlogUid());
        });
        Collection<Blog> blogCollection = null;
        if (blogUidList.size() > 0) {
            blogCollection = blogService.listByIds(blogUidList);
            if (blogCollection.size() > 0) {
                List<Blog> blogTempList = new ArrayList<>(blogCollection);
                List<Blog> blogList = blogService.setTagAndSortAndPictureByBlogList(blogTempList);
                Map<String, Blog> blogMap = new HashMap<>();
                blogList.forEach(item -> {
                    blogMap.put(item.getUid(), item);
                });
                subjectItemList.forEach(item -> {
                    item.setBlog(blogMap.get(item.getBlogUid()));
                });
                pageList.setRecords(subjectItemList);
            }
        }

        return pageList;
    }

    @Override
    public String addSubjectItemList(List<SubjectItemVO> subjectItemVOList) {
        List<String> blogUidList = new ArrayList<>();
        String subjectUid = "";
        for (SubjectItemVO subjectItemVO : subjectItemVOList) {
            blogUidList.add(subjectItemVO.getBlogUid());
            if (StringUtils.isEmpty(subjectUid) && StringUtils.isNotEmpty(subjectItemVO.getSubjectUid())) {
                subjectUid = subjectItemVO.getSubjectUid();
            }
        }
        // 查询SubjectItem中是否包含重复的博客
        QueryWrapper<SubjectItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.SUBJECT_UID, subjectUid);
        queryWrapper.in(SQLConstants.BLOG_UID, blogUidList);
        List<SubjectItem> repeatSubjectItemList = subjectItemService.list(queryWrapper);
        // 找出重复的博客UID
        List<String> repeatBlogList = new ArrayList<>();
        repeatSubjectItemList.forEach(item -> {
            repeatBlogList.add(item.getBlogUid());
        });

        List<SubjectItem> subjectItemList = new ArrayList<>();
        for (SubjectItemVO subjectItemVO : subjectItemVOList) {
            if (StringUtils.isEmpty(subjectItemVO.getSubjectUid()) || StringUtils.isEmpty(subjectItemVO.getBlogUid())) {
                return ResultUtil.errorWithMessage(MessageConstants.PARAM_INCORRECT);
            }
            // 判断是否重复添加
            if (repeatBlogList.contains(subjectItemVO.getBlogUid())) {
                continue;
            } else {
                SubjectItem subjectItem = new SubjectItem();
                subjectItem.setSubjectUid(subjectItemVO.getSubjectUid());
                subjectItem.setBlogUid(subjectItemVO.getBlogUid());
                subjectItem.setStatus(EStatus.ENABLE);
                subjectItemList.add(subjectItem);
            }
        }

        if (subjectItemList.size() <= 0) {
            if (repeatBlogList.size() == 0) {
                return ResultUtil.errorWithMessage(MessageConstants.INSERT_FAIL);
            } else {
                return ResultUtil.errorWithMessage(MessageConstants.INSERT_FAIL + "，已跳过" + repeatBlogList.size() + "个重复数据");
            }
        } else {
            subjectItemService.saveBatch(subjectItemList);
            if (repeatBlogList.size() == 0) {
                return ResultUtil.successWithMessage(MessageConstants.INSERT_SUCCESS);
            } else {
                return ResultUtil.successWithMessage(MessageConstants.INSERT_SUCCESS + "，已跳过" + repeatBlogList.size() + "个重复数据，成功插入" + (subjectItemVOList.size() - repeatBlogList.size()) + "条数据");
            }
        }
    }

    @Override
    public String editSubjectItemList(List<SubjectItemVO> subjectItemVOList) {
        List<String> subjectItemUidList = new ArrayList<>();
        subjectItemVOList.forEach(item -> {
            subjectItemUidList.add(item.getUid());
        });
        Collection<SubjectItem> subjectItemCollection = null;
        if (subjectItemUidList.size() > 0) {
            subjectItemCollection = subjectItemService.listByIds(subjectItemUidList);
            if (subjectItemCollection.size() > 0) {
                HashMap<String, SubjectItemVO> subjectItemVOHashMap = new HashMap<>();
                subjectItemVOList.forEach(item -> {
                    subjectItemVOHashMap.put(item.getUid(), item);
                });
                // 修改排序字段
                List<SubjectItem> subjectItemList = new ArrayList<>();
                subjectItemCollection.forEach(item -> {
                    SubjectItemVO subjectItemVO = subjectItemVOHashMap.get(item.getUid());
                    item.setSubjectUid(subjectItemVO.getSubjectUid());
                    item.setBlogUid(subjectItemVO.getBlogUid());
                    item.setStatus(EStatus.ENABLE);
                    item.setSort(subjectItemVO.getSort());
                    item.setUpdateTime(new Date());
                    subjectItemList.add(item);
                });
                subjectItemService.updateBatchById(subjectItemList);
            }
        }
        return ResultUtil.successWithMessage(MessageConstants.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchSubjectItem(List<SubjectItemVO> subjectItemVOList) {
        if (subjectItemVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConstants.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        subjectItemVOList.forEach(item -> {
            uids.add(item.getUid());
        });
        subjectItemService.removeByIds(uids);
        return ResultUtil.successWithMessage(MessageConstants.DELETE_SUCCESS);
    }

    @Override
    public String deleteBatchSubjectItemByBlogUid(List<String> blogUid) {
        boolean checkSuccess = StringUtils.checkUidList(blogUid);
        if (!checkSuccess) {
            throw new DeleteException(ErrorCode.DELETE_FAILED_PLEASE_CHECK_UID, MessageConstants.DELETE_FAILED_PLEASE_CHECK_UID);
        }
        QueryWrapper<SubjectItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(SQLConstants.BLOG_UID, blogUid);
        subjectItemService.remove(queryWrapper);
        return ResultUtil.successWithMessage(MessageConstants.DELETE_SUCCESS);
    }

    @Override
    public String sortByCreateTime(String subjectUid, Boolean isDesc) {
        QueryWrapper<SubjectItem> queryWrapper = new QueryWrapper();
        queryWrapper.eq(SQLConstants.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConstants.SUBJECT_UID, subjectUid);
        // 查询出所有的专题列表
        List<SubjectItem> subjectItemList = subjectItemService.list(queryWrapper);
        // 获取专题中的博客uid
        List<String> blogUidList = new ArrayList<>();
        subjectItemList.forEach(item -> {
            blogUidList.add(item.getBlogUid());
        });
        if(blogUidList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConstants.UPDATE_FAIL);
        }
        Collection<Blog> blogList = blogService.listByIds(blogUidList);
        List<Blog> tempBlogList = new ArrayList<>();
        // 升序排列或降序排列
        if(isDesc) {
            tempBlogList = blogList.stream().sorted(Comparator.comparing(Blog::getCreateTime).reversed()).collect(Collectors.toList());
        } else {
            tempBlogList = blogList.stream().sorted(Comparator.comparing(Blog::getCreateTime)).collect(Collectors.toList());
        }

        // 设置初始化最大的sort值
        int maxSort = tempBlogList.size();
        Map<String, Integer> subjectItemSortMap = new HashMap<>();
        for (Blog item : tempBlogList) {
            subjectItemSortMap.put(item.getUid(), maxSort--);
        }

        // 设置更新后的排序值
        for (SubjectItem item : subjectItemList) {
            item.setSort(subjectItemSortMap.get(item.getBlogUid()));
        }
        subjectItemService.updateBatchById(subjectItemList);
        return ResultUtil.successWithMessage(MessageConstants.OPERATION_SUCCESS);
    }
}
