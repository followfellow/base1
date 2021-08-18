package com.demo.base.jurisGroupManager.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.demo.base.jurisGroupManager.dto.JurisGroupDTO;
import com.demo.dbutils.BaseDAOHibernateImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * daoImpl
 *
 * @author:wxc
 * @date:2021-07-23 17:03:23
 */
@Repository
public class JurisGroupDaoImpl extends BaseDAOHibernateImpl implements JurisGroupDao {

    /**
     * 查询列表
     *
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    @Override
    public List<JurisGroupDTO> findJurisGroupList() {
        String sql = "select jurisGroupId,jurisGroupPid ,jurisGroupName" +
                " from sys_juris_group_t   ";
        return findObjectBySql(sql, JurisGroupDTO.class);
    }

    /**
     * 通过pid查询
     *
     * @param jurisGroupId
     * @author wxc
     * @date 2021/7/24 15:00
     */
    @Override
    public List<JurisGroupDTO> findJurisGroupByPid(Long jurisGroupId) {
        String sql = "select jurisGroupId" +
                " from sys_juris_group_t  " +
                "where  jurisGroupPid = " + jurisGroupId + " limit 1";
        return findObjectBySql(sql, JurisGroupDTO.class);
    }

    /**
     * 通过单位id查询分组
     *
     * @param businessId
     * @author wxc
     * @date 2021/7/26 15:11
     */
    @Override
    public JurisGroupDTO findGroupByBusinessId(Long businessId) {
        String sql = "select t1.jurisGroupId" +
                "  from sys_juris_group_t t1, sys_juris_group_org_t t2" +
                "  where  t1.jurisGroupId = t2.jurisGroupId " +
                "  and  t2.businessId = " + businessId;
        List<JurisGroupDTO> list = findObjectBySql(sql, JurisGroupDTO.class);
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 通过分组名称查询分组
     *
     * @param jurisGroupName
     * @param jurisGroupId
     * @author wxc
     * @date 2021/8/9 15:41
     */
    @Override
    public List<JurisGroupDTO> findJurisGroupByName(String jurisGroupName, Long jurisGroupId) {
        String sql = "select jurisGroupId" +
                " from sys_juris_group_t  where  jurisGroupName = '" + jurisGroupName + "'";
        if (jurisGroupId != null) {
            sql += " and jurisGroupId <> " + jurisGroupId;
        }
        sql += " limit 1";
        return findObjectBySql(sql, JurisGroupDTO.class);
    }

    /**
     * 通过分组id查询是否绑定单位
     *
     * @param jurisGroupId
     * @author wxc
     * @date 2021/8/9 16:08
     */
    @Override
    public List<JurisGroupDTO> findBusinessByJurisGroupId(Long jurisGroupId) {
        String sql = "select jurisGroupId" +
                " from sys_juris_group_org_t  where  jurisGroupId = " + jurisGroupId +
                " limit 1";
        return findObjectBySql(sql, JurisGroupDTO.class);
    }
}
