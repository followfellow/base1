package com.demo.base.roleManager.dao;

import com.demo.base.roleManager.dto.RoleDTO;
import com.demo.dbutils.BaseDAO;

import java.util.List;

/**
 * 角色 DAO
 *
 * @author sw
 * @date 2021-06-13 17:30:58
 */
public interface RoleDao extends BaseDAO {

    /**
     * 查询角色列表
     *
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    List<RoleDTO> findRoleList();


    /**
     * 根据pid查询角色
     *
     * @param rolePid
     * @author wxc
     * @date 2021/7/21 17:29
     */
    List<RoleDTO> findRoleByPid(Long rolePid);

    /**
     * 通过角色名称查找角色
     *
     * @param roleName
     * @param roleId
     * @author wxc
     * @date 2021/8/9 13:42
     */
    List<RoleDTO> findRoleByName(String roleName, Long roleId);

    /**
     * 查询当前用户下角色
     *
     * @param userId
     * @author wxc
     * @date 2021/8/9 14:19
     */
    List<RoleDTO> findRoleByUserId(Long userId);

    /**
     * 通过roleId查询用户
     *
     * @param roleId
     * @author wxc
     * @date 2021/8/9 16:12
     */
    List<RoleDTO> findUserByRoleId(Long roleId);
}
