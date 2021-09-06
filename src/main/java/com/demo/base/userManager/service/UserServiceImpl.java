package com.demo.base.userManager.service;

import cn.hutool.core.collection.CollectionUtil;
import com.demo.action.service.BaseServiceImpl;
import com.demo.action.vo.QueryPage;
import com.demo.base.roleManager.dto.RoleDTO;
import com.demo.base.userManager.dao.UserDao;
import com.demo.base.userManager.dto.UserDTO;
import com.demo.base.userManager.po.RoleJoinUserDO;
import com.demo.base.userManager.po.UserDO;
import com.demo.base.userManager.request.*;
import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

/**
 * 用户管理 ServiceImpl
 *
 * @author sw
 * @date 2021-06-13 09:36:21
 */
@Service
public class UserServiceImpl extends BaseServiceImpl implements UserService {

    @Resource
    private UserDao userDao;




    /**
     * 查询用户管理列表
     *
     * @param findUserParam
     * @return
     * @author sw
     * @date 2021-06-13 09:36:21
     */
    @Override
    public List<UserDTO> findUserList(FindUserParam findUserParam, QueryPage queryPage) {
        return userDao.findUserList(findUserParam, queryPage);
    }


    /**
     * 添加用户管理
     *
     * @param userDO
     * @return
     * @author sw
     * @date 2021-06-13 09:36:21
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void addUser(UserDO userDO) {
        //保存用户
        userDao.save(userDO);
    }

    /**
     * 根据 id 查询 用户管理
     *
     * @param userId
     * @return
     * @author sw
     * @date 2021-06-13 09:36:21
     */
    @Override
    public UserDO queryUserById(Long userId) {
        UserDO userDO = (UserDO) userDao.getEntityById(UserDO.class, userId);
        if (userDO != null && userDao.getCurrUserOrgId().equals(userDO.getBusinessId())) {
            return userDO;
        }
        return null;
    }

    /**
     * 修改用户管理
     *
     * @param userDO
     * @return
     * @author sw
     * @date 2021-06-13 09:36:21
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateUser(UserDO userDO) {
        userDao.update(userDO);
    }

    /**
     * 根据id 删除 用户管理
     *
     * @param deleteUserParam
     * @return
     * @author sw
     * @date 2021-06-13 09:36:21
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteUser(DeleteUserParam deleteUserParam) {
        String sql = " delete from sys_user_t " +
                " where userId = " + deleteUserParam.getUserId() +
                " and businessId = " + userDao.getCurrUserOrgId() +
                " limit 1";
        userDao.executeSql(sql);
    }

    /**
     * 验证登录账户是否存在
     *
     * @param userName
     * @return
     * @author:sw
     * @date 2021/6/15 11:52
     */
    @Override
    public Boolean checkUserName(String userName) {
        return userDao.checkUserName(userName);
    }

    /**
     * 保存角色用户
     *
     * @param roleJoinUserDOList
     * @author:sw
     * @date 2021/6/24 20:10
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void operateRoleUser(List<RoleJoinUserDO> roleJoinUserDOList) {
        //保存角色用户
        if (CollectionUtil.isNotEmpty(roleJoinUserDOList)) {
            roleJoinUserDOList.forEach(roleJoinUserDO -> userDao.save(roleJoinUserDO));
        }
    }

    /**
     * 查询角色下用户
     *
     * @param findRoleUserParam
     * @param queryPage
     * @return
     * @author:sw
     * @date 2021/6/24 20:16
     */
    @Override
    public List<UserDTO> findRoleUser(FindRoleUserParam findRoleUserParam, QueryPage queryPage) {
        return userDao.findRoleUser(findRoleUserParam, queryPage);
    }

    /**
     * 修改用户状态
     *
     * @param modifyUserStatusParam
     * @author wxc
     * @date 2021/7/22 13:38
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void modifyUserStatus(ModifyUserStatusParam modifyUserStatusParam) {
        String sql = " update sys_user_t " +
                " set flag = " + modifyUserStatusParam.getFlag() +
                " where userId = " + modifyUserStatusParam.getUserId() +
                " and businessId = " + userDao.getCurrUserOrgId() +
                " limit 1";
        userDao.executeSql(sql);
    }

    /**
     * 修改密码
     *
     * @param resetPasswordParam
     * @author wxc
     * @date 2021/7/22 13:39
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void resetPassword(ResetPasswordParam resetPasswordParam) {
        String sql = " update sys_user_t " +
                " set password = '" + StringEscapeUtils.escapeSql(resetPasswordParam.getPassword()) + "'" +
                " where userId = " + resetPasswordParam.getUserId() +
                " and businessId = " + userDao.getCurrUserOrgId() +
                " limit 1";
        userDao.executeSql(sql);
    }

    /**
     * 删除角色用户绑定
     *
     * @param deleteUserRoleParam
     * @author wxc
     * @date 2021/7/28 19:19
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteRoleUser(DeleteUserRoleParam deleteUserRoleParam) {
        String sql = " delete from  sys_role_join_user_t " +
                " where userId = " + deleteUserRoleParam.getUserId() +
                " and roleId = " + deleteUserRoleParam.getRoleId() +
                " and businessId = " + userDao.getCurrUserOrgId() +
                " limit 1";
        userDao.executeSql(sql);
    }

    /**
     * 通过用户id集合查询角色信息
     *
     * @param userIdList
     * @author wxc
     * @date 2021/8/7 16:56
     */
    @Override
    public List<RoleDTO> findRoleByUserIds(List<Long> userIdList) {
        return userDao.findRoleByUserIds(userIdList);
    }

    /**
     * 查询该单位下非该角色
     *
     * @param findRoleUserParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/28 19:27
     */
    @Override
    public List<UserDTO> findNoRoleUser(FindRoleUserParam findRoleUserParam, QueryPage queryPage) {
        return userDao.findNoRoleUser(findRoleUserParam, queryPage);
    }

    /**
     * 通过名称查询用户信息
     *
     * @param userName
     * @author wxc
     * @date 2021/8/31 9:08
     */
    @Override
    public UserDTO findUserByName(String userName) {
        return userDao.findUserByName(userName);
    }

    /**
     * 校验操作绑定用户是否属于该单位
     *
     * @param userIdList
     * @author wxc
     * @date 2021/9/6 11:46
     */
    @Override
    public boolean checkUsersIfInBusiness(List<Long> userIdList) {
        return CollectionUtil.isNotEmpty(userDao.findUserByIds(userIdList));
    }
}
