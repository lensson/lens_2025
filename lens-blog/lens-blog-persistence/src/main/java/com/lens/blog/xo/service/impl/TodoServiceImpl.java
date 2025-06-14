package com.lens.blog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lens.blog.xo.constant.MessageConstants;
import com.lens.blog.xo.constant.SQLConstants;
import com.lens.blog.xo.constant.SysConstants;
import com.lens.blog.mapper.TodoMapper;
import com.lens.blog.xo.service.TodoService;

import com.lens.blog.vo.TodoVO;
import com.lens.common.base.enums.EStatus;
import com.lens.common.core.utils.ResultUtil;
import com.lens.common.core.utils.StringUtils;
import com.lens.common.db.entity.Todo;
import com.lens.common.db.mybatis.serviceImpl.SuperServiceImpl;
import com.lens.common.web.holder.RequestHolder;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 待办事项表 服务实现类
 * </p>
 *
 * @author xuzhixiang
 * @since 2019年6月29日10:31:44
 */
@Service
public class TodoServiceImpl extends SuperServiceImpl<TodoMapper, Todo> implements TodoService {

    @Autowired
    TodoService todoService;

    @Resource
    TodoMapper todoMapper;

    @Override
    public void toggleAll(Integer done, String adminUid) {
        todoMapper.toggleAll(done, adminUid);
    }

    @Override
    public IPage<Todo> getPageList(TodoVO todoVO) {
        String adminUid = RequestHolder.getAdminUid();
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotEmpty(todoVO.getKeyword()) && !StringUtils.isEmpty(todoVO.getKeyword().trim())) {
            queryWrapper.like(SQLConstants.TEXT, todoVO.getKeyword().trim());
        }
        queryWrapper.eq(SQLConstants.ADMINUID, adminUid);

        //按时间顺序倒排
        queryWrapper.orderByDesc(SQLConstants.CREATE_TIME);

        Page<Todo> page = new Page<>();
        page.setCurrent(todoVO.getCurrentPage());
        page.setSize(todoVO.getPageSize());
        queryWrapper.eq(SQLConstants.STATUS, EStatus.ENABLE);
        IPage<Todo> pageList = todoService.page(page, queryWrapper);
        return pageList;
    }

    @Override
    public String addTodo(TodoVO todoVO) {
        String adminUid = RequestHolder.getAdminUid();
        Todo todo = new Todo();
        todo.setText(todoVO.getText());
        //默认未完成
        todo.setDone(false);
        todo.setAdminUid(adminUid);
        todo.insert();
        return ResultUtil.result(SysConstants.SUCCESS, MessageConstants.INSERT_SUCCESS);
    }

    @Override
    public String editTodo(TodoVO todoVO) {
        String adminUid = RequestHolder.getAdminUid();
        Todo todo = todoService.getById(todoVO.getUid());

        if (!todo.getAdminUid().equals(adminUid)) {
            return ResultUtil.result(SysConstants.ERROR, MessageConstants.ACCESS_NO_PRIVILEGE);
        }

        todo.setText(todoVO.getText());
        todo.setDone(todoVO.getDone());
        todo.setUpdateTime(new Date());
        todo.updateById();
        return ResultUtil.result(SysConstants.SUCCESS, MessageConstants.UPDATE_SUCCESS);
    }

    @Override
    public String deleteTodo(TodoVO todoVO) {
        String adminUid = RequestHolder.getAdminUid();
        Todo todo = todoService.getById(todoVO.getUid());

        if (!todo.getAdminUid().equals(adminUid)) {
            return ResultUtil.result(SysConstants.ERROR, MessageConstants.DATA_NO_PRIVILEGE);
        }

        todo.setStatus(EStatus.DISABLED);
        todo.setUpdateTime(new Date());
        todo.updateById();
        return ResultUtil.result(SysConstants.SUCCESS, MessageConstants.DELETE_SUCCESS);
    }

    @Override
    public String editBatchTodo(TodoVO todoVO) {
        String adminUid = RequestHolder.getAdminUid();
        if (todoVO.getDone()) {
            todoService.toggleAll(SysConstants.ONE, adminUid);
        } else {
            todoService.toggleAll(SysConstants.ZERO, adminUid);
        }
        return ResultUtil.result(SysConstants.SUCCESS, MessageConstants.UPDATE_SUCCESS);
    }

}
