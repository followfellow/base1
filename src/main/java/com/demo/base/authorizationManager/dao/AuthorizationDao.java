package com.demo.base.authorizationManager.dao;

import com.demo.system.menuManager.dto.MenuDTO;
import com.demo.system.menuManager.request.FindMenuParam;
import com.demo.dbutils.BaseDAO;

import java.util.List;

/**
 * DAO
 *
 * @author sw
 * @date 2021-06-19 10:14:27
 */
public interface AuthorizationDao extends BaseDAO {

    /**
     * 查询菜单列表
     *
     * @param findMenuParam
     * @return
     * @author sw
     * @date 2021-06-19 10:14:27
     */
    List<MenuDTO> findMenuList(FindMenuParam findMenuParam);


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
     * 获取当前公司管理员权限
     *
     * @param groupId
     * @author wxc
     * @date 2021/7/26 14:38
     * @return
     */
    List<MenuDTO> findMenuByGroup(Long groupId);
}
