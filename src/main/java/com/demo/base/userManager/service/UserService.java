package com.demo.base.userManager.service;

import com.demo.action.vo.QueryPage;
import com.demo.base.roleManager.dto.RoleDTO;
import com.demo.base.userManager.dto.UserDTO;
import com.demo.base.userManager.po.RoleJoinUserDO;
import com.demo.base.userManager.po.UserDO;
import com.demo.base.userManager.request.*;

import java.util.List;

/**
 * 用户管理 Service
 *
 * @author sw
 * @date 2021-06-13 09:36:21
 */
public interface UserService {

    /**
     * 查询用户管理列表
     *
     * @param findUserParam
     * @return
     * @author sw
     * @date 2021-06-13 09:36:21
     */
    List<UserDTO> findUserList(FindUserParam findUserParam, QueryPage queryPage);

    /**
     * 添加用户管理
     *
     * @param userDO
     * @return
     * @author sw
     * @date 2021-06-13 09:36:21
     */
    void addUser(UserDO userDO);


    /**
     * 根据 id 查询 用户管理
     *
     * @param userId
     * @return
     * @author sw
     * @date 2021-06-13 09:36:21
     */
    UserDO queryUserById(Long userId);

    /**
     * 修改用户管理
     *
     * @param updateUserParam
     * @return
     * @author sw
     * @date 2021-06-13 09:36:21
     */
    void updateUser(UserDO updateUserParam);

    /**
     * 根据id 删除 用户管理
     *
     * @param deleteUserParam
     * @return
     * @author sw
     * @date 2021-06-13 09:36:21
     */
    void deleteUser(DeleteUserParam deleteUserParam);

    /**
     * 验证登录账户是否存在
     *
     * @param userName
     * @return
     * @author:sw
     * @date 2021/6/15 11:52
     */
    Boolean checkUserName(String userName);

    /**
     * 保存角色用户
     *
     * @param roleJoinUserDOList
     * @author:sw
     * @date 2021/6/24 20:10
     */
    void operateRoleUser(List<RoleJoinUserDO> roleJoinUserDOList);

    /**
     * 查询角色下用户
     *
     * @param findRoleUserParam
     * @param queryPage
     * @author wxc
     * @date 2021/8/9 11:33
     */
    List<UserDTO> findRoleUser(FindRoleUserParam findRoleUserParam, QueryPage queryPage);

    /**
     * 修改用户状态
     *
     * @param modifyUserStatusParam
     * @author wxc
     * @date 2021/7/22 13:38
     */
    void modifyUserStatus(ModifyUserStatusParam modifyUserStatusParam);

    /**
     * 修改密码
     *
     * @param resetPasswordParam
     * @author wxc
     * @date 2021/7/22 13:39
     */
    void resetPassword(ResetPasswordParam resetPasswordParam);

    /**
     * 删除角色用户绑定
     *
     * @param deleteUserRoleParam
     * @author wxc
     * @date 2021/7/28 19:19
     */
    void deleteRoleUser(DeleteUserRoleParam deleteUserRoleParam);

    /**
     * 查询该单位下非该角色
     *
     * @param findRoleUserParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/28 19:27
     * @return
     */
    List<UserDTO> findNoRoleUser(FindRoleUserParam findRoleUserParam, QueryPage queryPage);

    /**
     * 通过用户id集合查询角色信息
     *
     * @param userIdList
     * @author wxc
     * @date 2021/8/7 16:56
     */
    List<RoleDTO> findRoleByUserIds(List<Long> userIdList);

    /**
     * 通过名称查询用户信息
     *
     * @param userName
     * @author wxc
     * @date 2021/8/31 9:08
     */
    UserDTO findUserByName(String userName);
}
