package com.lens.blog.xo.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lens.blog.entity.ExceptionLog;
import com.lens.blog.vo.ExceptionLogVO;
import com.lens.common.db.mybatis.service.SuperService;

/**
 * 操作异常日志 服务类
 *
 * @author limbo
 * @date 2018-09-30
 */
public interface ExceptionLogService extends SuperService<ExceptionLog> {

    /**
     * 获取异常日志列表
     *
     * @param exceptionLogVO
     * @return
     */
    public IPage<ExceptionLog> getPageList(ExceptionLogVO exceptionLogVO);
}
