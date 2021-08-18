package com.demo.base.roleManager.service;

import com.demo.base.roleManager.po.RoleDO;
import com.demo.base.roleManager.dto.RoleDTO;

import java.util.List;

/**
 * 角色 Service
 *
 * @author sw
 * @date 2021-06-13 17:30:58
 */
public interface RoleService {

    /**
     * 查询角色列表
     *
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    List<RoleDTO> findRoleList();

    /**
     * 添加角色
     *
     * @param roleDO
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    void addRole(RoleDO roleDO);


    /**
     * 根据 id 查询 角色
     *
     * @param roleId
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    RoleDO queryRoleById(Long roleId);

    /**
     * 修改角色
     *
     * @param roleDO
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    void updateRole(RoleDO roleDO);

    /**
     * 根据id 删除 角色
     *
     * @param roleId
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    void deleteRole(Long roleId);


    /**
     * 校验是否存在下级角色
     *
     * @param rolePid
     * @author wxc
     * @date 2021/7/21 17:20
     */
    boolean checkIfHasChild(Long rolePid);

    /**
     * 查询当前用户角色
     *
     * @param userId
     * @author wxc
     * @date 2021/7/26 13:34
     */
    List<RoleDTO> findRoleByUserId(Long userId);

    /**
     * 校验角色名是否存在
     *
     * @param roleName
     * @param roleId
     * @author wxc
     * @date 2021/8/9 13:41
     */
    boolean checkNameIfExist(String roleName, Long roleId);

    /**
     * 校验角色是否分配了用户
     *
     * @param roleId
     * @author wxc
     * @date 2021/8/9 16:11
     */
    boolean checkUserIfExist(Long roleId);
}
