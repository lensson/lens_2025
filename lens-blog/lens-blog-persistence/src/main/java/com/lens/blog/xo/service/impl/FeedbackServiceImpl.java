package com.lens.blog.xo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lens.blog.entity.Feedback;
import com.lens.blog.entity.User;
import com.lens.blog.xo.mapper.FeedbackMapper;
import com.lens.blog.vo.FeedbackVO;
import com.lens.blog.xo.constant.MessageConstants;
import com.lens.blog.xo.constant.SysConstants;
import com.lens.blog.xo.service.FeedbackService;
import com.lens.blog.xo.service.UserService;
import com.lens.common.base.enums.EStatus;
import com.lens.common.core.utils.ResultUtil;
import com.lens.common.core.utils.StringUtils;
import com.lens.common.db.mybatis.plugin.query.LambdaQueryWrapperPlus;
import com.lens.common.db.mybatis.serviceImpl.SuperServiceImpl;
import com.lens.common.web.holder.RequestHolder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 反馈表 服务实现类
 *
 * @author 陌溪
 * @date 2018-09-08
 */
@Service
public class FeedbackServiceImpl extends SuperServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private UserService userService;

    @Override
    public IPage<Feedback> getPageList(FeedbackVO feedbackVO) {
        LambdaQueryWrapperPlus<Feedback> queryWrapper = new LambdaQueryWrapperPlus<>();
        queryWrapper.like(Feedback::getTitle, feedbackVO.getTitle());
        queryWrapper.eq(Feedback::getFeedbackStatus, feedbackVO.getFeedbackStatus());
        queryWrapper.eq(Feedback::getStatus, EStatus.ENABLE);
        queryWrapper.orderByDesc(Feedback::getCreateTime);

        Page<Feedback> page = new Page<>();
        page.setCurrent(feedbackVO.getCurrentPage());
        page.setSize(feedbackVO.getPageSize());

        IPage<Feedback> pageList = feedbackService.page(page, queryWrapper);

        List<Feedback> feedbackList = pageList.getRecords();
        List<String> userUids = feedbackList.stream().filter(item -> StringUtils.isNotEmpty(item.getUserUid()))
                .map(Feedback::getUserUid).collect(Collectors.toList());
        List<User> userList = userService.getUserListByIds(userUids);
        Map<String, User> map = new HashMap<>();
        userList.forEach(item -> {
            item.setPassWord("");
            map.put(item.getUid(), item);
        });

        feedbackList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(map.get(item.getUserUid()));
            }
        });

        pageList.setRecords(feedbackList);
        return pageList;
    }

    @Override
    public String addFeedback(FeedbackVO feedbackVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        Feedback feedback = feedbackService.getById(feedbackVO.getUid());
        feedback.setTitle(feedbackVO.getTitle());
        feedback.setContent(feedbackVO.getContent());
        feedback.setFeedbackStatus(feedbackVO.getFeedbackStatus());
        feedback.setReply(feedbackVO.getReply());
        feedback.setUpdateTime(new Date());
        if (request.getAttribute(SysConstants.ADMIN_UID) != null) {
            feedback.setAdminUid(request.getAttribute(SysConstants.ADMIN_UID).toString());
        }
        feedback.setUpdateTime(new Date());
        feedback.updateById();
        return ResultUtil.result(SysConstants.SUCCESS, MessageConstants.UPDATE_SUCCESS);
    }

    @Override
    public String editFeedback(FeedbackVO feedbackVO) {
        return null;
    }

    @Override
    public String deleteBatchFeedback(List<FeedbackVO> feedbackVOList) {
        HttpServletRequest request = RequestHolder.getRequest();
        final String adminUid = request.getAttribute(SysConstants.ADMIN_UID).toString();
        if (feedbackVOList.size() <= 0) {
            return ResultUtil.result(SysConstants.ERROR, MessageConstants.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        feedbackVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<Feedback> feedbackList = feedbackService.listByIds(uids);

        feedbackList.forEach(item -> {
            item.setAdminUid(adminUid);
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });

        Boolean save = feedbackService.updateBatchById(feedbackList);

        if (save) {
            return ResultUtil.result(SysConstants.SUCCESS, MessageConstants.DELETE_SUCCESS);
        } else {
            return ResultUtil.result(SysConstants.ERROR, MessageConstants.DELETE_FAIL);
        }
    }
}
