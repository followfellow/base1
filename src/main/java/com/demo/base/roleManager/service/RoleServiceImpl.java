package com.demo.base.roleManager.service;

import cn.hutool.core.collection.CollectionUtil;
import com.demo.action.service.BaseServiceImpl;
import com.demo.base.roleManager.dao.RoleDao;
import com.demo.base.roleManager.dto.RoleDTO;
import com.demo.base.roleManager.po.RoleDO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

/**
 * 角色 ServiceImpl
 *
 * @author sw
 * @date 2021-06-13 17:30:58
 */
@Service
public class RoleServiceImpl extends BaseServiceImpl implements RoleService {

    @Resource
    private RoleDao roleDao;

    /**
     * 查询角色列表
     *
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    @Override
    public List<RoleDTO> findRoleList() {
        return roleDao.findRoleList();
    }

    /**
     * 添加角色
     *
     * @param roleDO
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void addRole(RoleDO roleDO) {
        roleDao.save(roleDO);
    }

    /**
     * 根据 id 查询 角色
     *
     * @param roleId
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    @Override
    public RoleDO queryRoleById(Long roleId) {
        RoleDO roleDO = (RoleDO) roleDao.getEntityById(RoleDO.class, roleId);
        if (roleDO != null && roleDao.getCurrUserOrgId().equals(roleDO.getBusinessId())) {
            return roleDO;
        }
        return null;
    }

    /**
     * 修改角色
     *
     * @param roleDO
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateRole(RoleDO roleDO) {
        roleDao.update(roleDO);
    }

    /**
     * 根据id 删除 角色
     *
     * @param roleId
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteRole(Long roleId) {
        Long businessId = roleDao.getCurrUserOrgId();
        String sql = "delete from sys_role_t where roleId = " + roleId + " and businessId = " + businessId + " limit 1";
        roleDao.executeSql(sql);
        sql = "delete from sys_role_menu_t where roleId = " + roleId + " and businessId = " + businessId;
        roleDao.executeSql(sql);
    }


    /**
     * 校验是否存在下级角色
     *
     * @param rolePid
     * @author wxc
     * @date 2021/7/21 17:20
     */
    @Override
    public boolean checkIfHasChild(Long rolePid) {
        return !CollectionUtil.isEmpty(roleDao.findRoleByPid(rolePid));
    }

    /**
     * 查询当前用户角色
     *
     * @param userId
     * @author wxc
     * @date 2021/7/26 13:34
     */
    @Override
    public List<RoleDTO> findRoleByUserId(Long userId) {
        return roleDao.findRoleByUserId(userId);
    }

    /**
     * 校验角色名是否存在
     *
     * @param roleName
     * @param roleId
     * @author wxc
     * @date 2021/8/9 13:41
     */
    @Override
    public boolean checkNameIfExist(String roleName, Long roleId) {
        return CollectionUtil.isNotEmpty(roleDao.findRoleByName(roleName, roleId));
    }

    /**
     * 校验角色是否分配了用户
     *
     * @param roleId
     * @author wxc
     * @date 2021/8/9 16:11
     */
    @Override
    public boolean checkUserIfExist(Long roleId) {
        return CollectionUtil.isNotEmpty(roleDao.findUserByRoleId(roleId));
    }
}
