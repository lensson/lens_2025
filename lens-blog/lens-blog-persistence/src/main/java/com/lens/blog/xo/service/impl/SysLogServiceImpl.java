package com.lens.blog.xo.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lens.blog.xo.constant.SysConstants;
import com.lens.blog.mapper.SysLogMapper;
import com.lens.blog.xo.service.SysLogService;
import com.lens.blog.vo.SysLogVO;
import com.lens.common.base.constant.Constants;
import com.lens.common.base.enums.EStatus;
import com.lens.common.core.utils.DateUtils;
import com.lens.common.core.utils.StringUtils;
import com.lens.common.db.entity.SysLog;
import com.lens.common.db.mybatis.plugin.query.LambdaQueryWrapperPlus;
import com.lens.common.db.mybatis.serviceImpl.SuperServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 操作日志 服务实现类
 * </p>
 *
 * @author limbo
 * @since 2018-09-30
 */
@Service
public class SysLogServiceImpl extends SuperServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    @Autowired
    SysLogService sysLogService;

    @Override
    public IPage<SysLog> getPageList(SysLogVO sysLogVO) {

        LambdaQueryWrapperPlus<SysLog> queryWrapper = new LambdaQueryWrapperPlus<>();
        queryWrapper.eq(SysLog::getUserName, sysLogVO.getUserName().trim());
        queryWrapper.eq(SysLog::getOperation, sysLogVO.getOperation());
        queryWrapper.eq(SysLog::getIp, sysLogVO.getIp());


        if (StringUtils.isNotBlank(sysLogVO.getStartTime())) {
            String[] time = sysLogVO.getStartTime().split(SysConstants.FILE_SEGMENTATION);
            if (time.length == Constants.NUM_TWO) {
                queryWrapper.between(SysLog::getCreateTime, DateUtils.str2Date(time[0]), DateUtils.str2Date(time[1]));
            }
        }

        if (StringUtils.isNotBlank(sysLogVO.getSpendTimeStr())) {
            String[] spendTimeList = StringUtils.split(sysLogVO.getSpendTimeStr(), Constants.SYMBOL_UNDERLINE);
            if (spendTimeList.length == Constants.NUM_TWO) {
                queryWrapper.between(SysLog::getSpendTime, Integer.valueOf(spendTimeList[0]), Integer.valueOf(spendTimeList[1]));
            }
        }

        Page<SysLog> page = new Page<>();
        page.setCurrent(sysLogVO.getCurrentPage());
        page.setSize(sysLogVO.getPageSize());
        queryWrapper.eq(SysLog::getStatus, EStatus.ENABLE);
        queryWrapper.orderByDesc(SysLog::getCreateTime);
        IPage<SysLog> pageList = sysLogService.page(page, queryWrapper);
        return pageList;
    }
}
