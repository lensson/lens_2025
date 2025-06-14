package com.lens.blog.xo.service;


import com.lens.blog.vo.RoleVO;
import com.lens.common.db.entity.Role;
import com.lens.common.db.mybatis.service.SuperService;

/**
 * 角色表 服务类
 *
 * @author 陌溪
 * @date 2018-09-04
 */
public interface RoleService extends SuperService<Role> {

    /**
     * 新增角色
     *
     * @param roleVO
     */
    public String addRole(RoleVO roleVO);

    /**
     * 编辑角色
     *
     * @param roleVO
     */
    public String editRole(RoleVO roleVO);

    /**
     * 删除角色
     *
     * @param roleVO
     */
    public String deleteRole(RoleVO roleVO);

}
