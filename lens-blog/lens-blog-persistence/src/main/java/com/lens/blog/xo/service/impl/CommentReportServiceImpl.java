package com.lens.blog.xo.service.impl;


import com.lens.blog.entity.CommentReport;
import com.lens.blog.xo.mapper.CommentReportMapper;
import com.lens.blog.xo.service.CommentReportService;
import com.lens.common.db.mybatis.serviceImpl.SuperServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 评论举报表 服务实现类
 *
 * @author 陌溪
 * @date 2020年1月12日15:47:47
 */
@Service
public class CommentReportServiceImpl extends SuperServiceImpl<CommentReportMapper, CommentReport> implements CommentReportService {

}
