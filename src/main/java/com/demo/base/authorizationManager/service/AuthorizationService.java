package com.demo.base.authorizationManager.service;

import com.demo.base.authorizationManager.po.JurisGroupMenuDO;
import com.demo.base.authorizationManager.po.RoleMenuDO;
import com.demo.system.menuManager.dto.MenuDTO;
import com.demo.system.menuManager.request.FindMenuParam;

import java.util.List;

/**
 * 菜单 Service
 *
 * @author sw
 * @date 2021-06-19 10:14:27
 */
public interface AuthorizationService {



    /**
     * 根据角色id 查询 菜单信息
     *
     * @param roleId
     * @return
     * @author:sw
     * @date 2021/6/24 8:35
     */
    List<MenuDTO> findMenuByRole(Long roleId);

    /**
     * 操作角色菜单授权
     *
     * @param roleId
     * @param roleMenuDOList
     * @author:sw
     * @date 2021/6/24 13:13
     */
    void operateRoleMenu(Long roleId, List<RoleMenuDO> roleMenuDOList);


    /**
     * 获取当前公司系统管理员权限
     *
     * @param groupId
     * @author wxc
     * @date 2021/7/26 14:36
     * @return
     */
    List<MenuDTO> findMenuByGroup(Long groupId);

    /**
     * 操作分组权限
     *
     * @param jurisGroupId
     * @param addJurisGroupMenuList
     * @param deleteMenuNos
     * @author wxc
     * @date 2021/7/28 13:34
     */
    void operateGroupMenu(Long jurisGroupId, List<JurisGroupMenuDO> addJurisGroupMenuList, List<String> deleteMenuNos);
}
