package com.demo.base.authorizationManager.service;

import cn.hutool.core.collection.CollectionUtil;
import com.demo.action.service.BaseServiceImpl;
import com.demo.base.authorizationManager.dao.AuthorizationDao;
import com.demo.base.authorizationManager.po.JurisGroupMenuDO;
import com.demo.base.authorizationManager.po.RoleMenuDO;
import com.demo.system.menuManager.dto.MenuDTO;
import com.demo.utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

/**
 * 菜单 ServiceImpl
 *
 * @author sw
 * @date 2021-06-19 10:14:27
 */
@Service
public class AuthorizationServiceImpl extends BaseServiceImpl implements AuthorizationService {

    @Resource
    private AuthorizationDao authorizationDao;


    /**
     * 通过角色id查询菜单
     *
     * @param roleId
     * @return
     * @author wxc
     * @date 2021/7/26 15:43
     */
    @Override
    public List<MenuDTO> findMenuByRole(Long roleId) {
        return authorizationDao.findMenuByRole(roleId);
    }

    /**
     * 删除角色菜单权限
     *
     * @param roleId
     * @param roleMenuDOList
     * @author wxc
     * @date 2021/7/28 9:34
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void operateRoleMenu(Long roleId, List<RoleMenuDO> roleMenuDOList) {
        //删除角色原来的菜单权限
        String sql = "delete from sys_role_menu_t where roleId = " + roleId + " and businessId = " + authorizationDao.getCurrUserOrgId();
        authorizationDao.executeSql(sql);
        //保存角色菜单
        for (RoleMenuDO roleMenuDO : roleMenuDOList) {
            authorizationDao.save(roleMenuDO);
        }
    }

    /**
     * 获取当前公司系统管理员权限
     *
     * @param groupId
     * @return
     * @author wxc
     * @date 2021/7/26 14:36
     */
    @Override
    public List<MenuDTO> findMenuByGroup(Long groupId) {
        return authorizationDao.findMenuByGroup(groupId);
    }

    /**
     * 操作分组权限
     *
     * @param jurisGroupId
     * @param addJurisGroupMenuList
     * @param deleteMenuNos
     * @author wxc
     * @date 2021/7/28 13:34
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void operateGroupMenu(Long jurisGroupId, List<JurisGroupMenuDO> addJurisGroupMenuList, List<String> deleteMenuNos) {
        //先删除
        if (CollectionUtil.isNotEmpty(deleteMenuNos)) {
            String delMenus = StringUtils.join("','", deleteMenuNos);
            String sql = " delete from sys_juris_group_menu_t " +
                    " where jurisGroupId = " + jurisGroupId +
                    " and menuNo in ('" + delMenus + "')";
            //删除分组权限表
            authorizationDao.executeSql(sql);
            sql = " delete t1 " +
                    " from  sys_role_menu_t t1, sys_juris_group_org_t t2" +
                    " where t1.businessId = t2.businessId " +
                    " and t2.jurisGroupId = " + jurisGroupId +
                    " and t1.menuNo in ('" + delMenus + "')";
            //删除角色权限表
            authorizationDao.executeSql(sql);
        }
        //再新增
        if (CollectionUtil.isNotEmpty(addJurisGroupMenuList)) {
            addJurisGroupMenuList.forEach(item -> authorizationDao.save(item));
        }

    }
}
