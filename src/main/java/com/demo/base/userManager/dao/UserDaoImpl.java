package com.demo.base.userManager.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.demo.action.vo.QueryPage;
import com.demo.base.roleManager.dto.RoleDTO;
import com.demo.base.userManager.dto.UserDTO;
import com.demo.base.userManager.request.FindRoleUserParam;
import com.demo.base.userManager.request.FindUserParam;
import com.demo.contants.CodeConstants;
import com.demo.dbutils.BaseDAOHibernateImpl;
import com.demo.utils.StringUtils;
import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户管理 DaoMysqlImpl
 *
 * @author sw
 * @date 2021-06-13 09:36:21
 */
@Repository
public class UserDaoImpl extends BaseDAOHibernateImpl implements UserDao {
    /**
     * 查询运营用户列表
     *
     * @param findUserParam
     * @param queryPage
     * @return
     * @author wxc
     * @date 2021/7/28 19:10
     */
    @Override
    public List<UserDTO> findUserList(FindUserParam findUserParam, QueryPage queryPage) {
        String sql = " select userId,userName,userRealName,cellphone,validTime,flag" +
                " from sys_user_t  " +
                " where userIdentity = " + CodeConstants.USER_IDENTITY_PTYH +
                " and businessId = " + getCurrUserOrgId();
        if (findUserParam != null && StringUtils.isNotBlank(findUserParam.getCondition())) {
            String condition = StringEscapeUtils.escapeSql(findUserParam.getCondition());
            sql += " and (userName like '%" + condition + "%' or" +
                    " userRealName like '%" + condition + "%' or " +
                    " cellphone = '" + condition + "' )";
        }
        sql += " order by updatedDate desc ";
        return findObjectBySql(sql, UserDTO.class, queryPage);
    }


    /**
     * 验证用户名是否存在
     *
     * @param userName
     * @author wxc
     * @date 2021/7/28 19:13
     */
    @Override
    public Boolean checkUserName(String userName) {
        String sql = "select userId from sys_user_t  where userName = '" + StringEscapeUtils.escapeSql(userName) + "'  limit 1";
        return CollectionUtil.isNotEmpty(findBySQL(sql));
    }

    /**
     * 通过角色ID查询相关用户
     *
     * @param findRoleUserParam
     * @param queryPage
     * @return
     * @author wxc
     * @date 2021/7/28 19:01
     */
    @Override
    public List<UserDTO> findRoleUser(FindRoleUserParam findRoleUserParam, QueryPage queryPage) {
        String sql = "select t1.userId,t1.userName,t1.userRealName " +
                " from  sys_user_t t1," +
                " sys_role_join_user_t t2" +
                " where t1.userId = t2.userId " +
                " and t1.userIdentity = " + CodeConstants.USER_IDENTITY_PTYH +
                " and t1.businessId = " + getCurrUserOrgId() +
                " and  t2.roleId =  " + findRoleUserParam.getRoleId();
        if (StringUtils.isNotBlank(findRoleUserParam.getCondition())) {
            String condition = StringEscapeUtils.escapeSql(findRoleUserParam.getCondition());
            sql += " and ( userName like '%" + condition + "%' or " +
                    " userRealName like '%" + condition + "%' ) ";
        }
        return findObjectBySql(sql, UserDTO.class, queryPage);
    }

    /**
     * 查询该单位下非该角色用户信息
     *
     * @param findRoleUserParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/28 19:28
     */
    @Override
    public List<UserDTO> findNoRoleUser(FindRoleUserParam findRoleUserParam, QueryPage queryPage) {
        String sql = "select a.userId,a.userName,a.userRealName  " +
                " from  sys_user_t  a" +
                " where  a.businessId = " + getCurrUserOrgId() +
                " and a.userIdentity = " + CodeConstants.USER_IDENTITY_PTYH;
        if (StringUtils.isNotBlank(findRoleUserParam.getCondition())) {
            String condition = StringEscapeUtils.escapeSql(findRoleUserParam.getCondition());
            sql += " and ( userName like '%" + condition + "%' or " +
                    " userRealName like '%" + condition + "%' ) ";
        }
        sql += " and not exists " +
                " ( select b.roleJoinUserId  " +
                " from  sys_role_join_user_t b" +
                " where a.userId = b.userId" +
                " and b.roleId = " + findRoleUserParam.getRoleId() + " " +
                " and b.businessId =  " + getCurrUserOrgId() + "  )  ";
        return findObjectBySql(sql, UserDTO.class, queryPage);
    }


    /**
     * 通过用户id集合获取角色信息
     *
     * @param userIdList
     * @author wxc
     * @date 2021/8/7 16:59
     */
    @Override
    public List<RoleDTO> findRoleByUserIds(List<Long> userIdList) {
        StringBuilder userIds = new StringBuilder();
        userIdList.forEach(userId -> userIds.append(userId).append(","));
        String sql = "select t1.userId,t2.roleName from sys_role_join_user_t t1 " +
                "left join sys_role_t t2 on t1.roleId = t2.roleId " +
                "where t1.userId  in (" + userIds.substring(0, userIds.length() - 1) + ")";
        return findObjectBySql(sql, RoleDTO.class);
    }

    /**
     * @param userName
     * @author wxc
     * @date 2021/8/31 9:09
     */
    @Override
    public UserDTO findUserByName(String userName) {
        String sql = " select t1.userId,t1.userName,t1.userRealName,t1.businessId,t2.businessAllName" +
                " from sys_user_t t1,org_business_t t2   " +
                " where t1.userName = '" + userName + "' " +
                " and t1.businessId = t2.businessId" +
                " limit 1";
        List<UserDTO> list = findObjectBySql(sql, UserDTO.class);
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }
}
