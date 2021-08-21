package com.demo.base.roleManager.dao;

import com.demo.base.roleManager.dto.RoleDTO;
import com.demo.dbutils.BaseDAOHibernateImpl;
import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色 DaoMysqlImpl
 *
 * @author sw
 * @date 2021-06-13 17:30:58
 */
@Repository
public class RoleDaoMysqlImpl extends BaseDAOHibernateImpl implements RoleDao {

    /**
     * 查询角色列表
     *
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    @Override
    public List<RoleDTO> findRoleList() {
        String sql = " select roleId,roleName,rolePid from sys_role_t where 1 = 1";
        sql += " and businessId = " + getCurrUserOrgId();
        return findObjectBySql(sql, RoleDTO.class);
    }


    /**
     * 根据pid查询角色
     *
     * @param rolePid
     * @author wxc
     * @date 2021/7/21 17:29
     */
    @Override
    public List<RoleDTO> findRoleByPid(Long rolePid) {
        String sql = "select  roleId from  sys_role_t  where rolePid = " + rolePid;
        return findObjectBySql(sql, RoleDTO.class);
    }

    /**
     * 通过角色名称查找角色
     *
     * @param roleName
     * @param roleId
     * @author wxc
     * @date 2021/8/9 13:42
     */
    @Override
    public List<RoleDTO> findRoleByName(String roleName, Long roleId) {
        String sql = " select  roleId from  sys_role_t  where roleName = '" + StringEscapeUtils.escapeSql(roleName) + "' ";
        if (roleId != null) {
            sql += " and roleId <> " + roleId;
        }
        sql += " and businessId = " + getCurrUserOrgId();
        sql += " limit 1  ";
        return findObjectBySql(sql, RoleDTO.class);
    }

    /**
     * 查询当前用户下角色
     *
     * @param userId
     * @author wxc
     * @date 2021/8/9 14:19
     */
    @Override
    public List<RoleDTO> findRoleByUserId(Long userId) {
        String sql = " select t2.roleId,t2.roleName,t2.rolePid from sys_role_join_user_t t1 " +
                " left join sys_role_t t2 on t1.roleId = t2.roleId " +
                " where t1.userId  = " + userId;
        return findObjectBySql(sql, RoleDTO.class);
    }

    /**
     * 通过roleId查询用户
     *
     * @param roleId
     * @author wxc
     * @date 2021/8/9 16:12
     */
    @Override
    public List<RoleDTO> findUserByRoleId(Long roleId) {
        String sql = " select roleId from sys_role_join_user_t" +
                " where roleId = " + roleId +
                " limit 1";
        return findObjectBySql(sql, RoleDTO.class);
    }
}
