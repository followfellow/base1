package com.demo.base.authorizationManager.dao;

import com.demo.system.menuManager.dto.MenuDTO;
import com.demo.system.menuManager.request.FindMenuParam;
import com.demo.contants.CodeConstants;
import com.demo.dbutils.BaseDAOHibernateImpl;
import com.demo.utils.StringUtils;
import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜单 DaoMysqlImpl
 *
 * @author sw
 * @date 2021-06-19 10:14:27
 */
@Repository
public class AuthorizationDaoImpl extends BaseDAOHibernateImpl implements AuthorizationDao {
    /**
     * 查询菜单列表
     *
     * @param findMenuParam
     * @author wxc
     * @date 2021/7/26 15:43
     * @return
     */
    @Override
    public List<MenuDTO> findMenuList(FindMenuParam findMenuParam) {
        String sql = "select menuId,menuName,menuRoute,menuNo,menuPid,pathType,menuPath,component,hidden,menuIcon,menuSort,menuCategory,formatType,remark " +
                "from pub_menu_t where 1 = 1  and markDelete = " + CodeConstants.MARK_DELETE_NO;
        if (findMenuParam != null) {
            if (!StringUtils.isEmpty(findMenuParam.getMenuName())) {
                sql += " and menuName = '" + StringEscapeUtils.escapeSql(findMenuParam.getMenuName()) + "' ";
            }
            if (!StringUtils.isEmpty(findMenuParam.getMenuRoute())) {
                sql += " and menuRoute = '" + StringEscapeUtils.escapeSql(findMenuParam.getMenuRoute()) + "' ";
            }
            if (!StringUtils.isEmpty(findMenuParam.getMenuNo())) {
                sql += " and menuNo = '" + StringEscapeUtils.escapeSql(findMenuParam.getMenuNo()) + "' ";
            }
        }
        return findObjectBySql(sql, MenuDTO.class);
    }

    /**
     * 通过角色id查询菜单
     *
     * @param roleId
     * @author wxc
     * @date 2021/7/28 9:33
     * @return
     */
    @Override
    public List<MenuDTO> findMenuByRole(Long roleId) {
        String sql = "select  t1.menuId,t1.menuName,t1.menuRoute,t1.menuNo,t1.menuPid,t1.pathType,t1.menuPath,t1.component,t1.hidden,t1.menuIcon,t1.menuSort,t1.menuCategory,t1.formatType,t1.remark " +
                " from pub_menu_t t1, sys_role_menu_t t2 " +
                " where t1.menuNo = t2.menuNo" +
                " and t2.roleId = " + roleId;
        return findObjectBySql(sql, MenuDTO.class);
    }

    /**
     * 获取当前公司管理员权限
     *
     * @param groupId
     * @author wxc
     * @date 2021/7/26 14:38
     * @return
     */
    @Override
    public List<MenuDTO> findMenuByGroup(Long groupId) {
        String sql = "select  t1.menuId,t1.menuName,t1.menuRoute,t1.menuNo,t1.menuPid,t1.pathType,t1.menuPath,t1.component,t1.hidden,t1.menuIcon,t1.menuSort,t1.menuCategory,t1.formatType,t1.remark " +
                " from pub_menu_t t1, sys_juris_group_menu_t t2 " +
                " where t1.menuNo = t2.menuNo" +
                " and t2.jurisGroupId = " + groupId;
        return findObjectBySql(sql, MenuDTO.class);
    }

}
