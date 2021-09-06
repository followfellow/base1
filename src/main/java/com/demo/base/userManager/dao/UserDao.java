package com.demo.base.userManager.dao;

import com.demo.action.vo.QueryPage;
import com.demo.base.roleManager.dto.RoleDTO;
import com.demo.base.userManager.dto.UserDTO;
import com.demo.base.userManager.request.FindRoleUserParam;
import com.demo.base.userManager.request.FindUserParam;
import com.demo.dbutils.BaseDAO;

import java.util.List;

/**
 * 用户管理 DAO
 *
 * @author sw
 * @date 2021-06-13 09:36:21
 */
public interface UserDao extends BaseDAO {

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
     * 验证登录账户是否存在
     *
     * @param userName
     * @return
     * @author:sw
     * @date 2021/6/15 11:52
     */
    Boolean checkUserName(String userName);

    /**
     * 查询角色下用户
     *
     * @param findRoleUserParam
     * @param queryPage
     * @author wxc
     * @date 2021/8/9 11:34
     */
    List<UserDTO> findRoleUser(FindRoleUserParam findRoleUserParam, QueryPage queryPage);

    /**
     * 查询该单位下非该角色用户信息
     *
     * @param findRoleUserParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/28 19:28
     */
    List<UserDTO> findNoRoleUser(FindRoleUserParam findRoleUserParam, QueryPage queryPage);

    /**
     * 通过用户id集合获取角色信息
     *
     * @param userIdList
     * @author wxc
     * @date 2021/8/7 16:59
     */
    List<RoleDTO> findRoleByUserIds(List<Long> userIdList);

    /**
     * @param userName
     * @author wxc
     * @date 2021/8/31 9:09
     */
    UserDTO findUserByName(String userName);

    /**
     * @param userIdList
     * @author wxc
     * @date 2021/9/6 11:47
     */
    List<UserDTO> findUserByIds(List<Long> userIdList);
}
