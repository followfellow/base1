package com.demo.base.jurisGroupManager.dao;

import com.demo.base.jurisGroupManager.dto.JurisGroupDTO;
import com.demo.dbutils.BaseDAO;

import java.util.List;

/**
 * dao
 *
 * @author:wxc
 * @date:2021-07-23 17:03:23
 */
public interface JurisGroupDao extends BaseDAO {
    /**
     * 分页查询列表
     *
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    List<JurisGroupDTO> findJurisGroupList();

    /**
     * 通过pid查询
     *
     * @param jurisGroupId
     * @author wxc
     * @date 2021/7/24 15:00
     */
    List<JurisGroupDTO> findJurisGroupByPid(Long jurisGroupId);

    /**
     * 通过单位id查询分组
     *
     * @param businessId
     * @author wxc
     * @date 2021/7/26 15:11
     */
    JurisGroupDTO findGroupByBusinessId(Long businessId);

    /**
     * 通过分组名称查询分组
     *
     * @param jurisGroupName
     * @param jurisGroupId
     * @author wxc
     * @date 2021/8/9 15:41
     */
    List<JurisGroupDTO> findJurisGroupByName(String jurisGroupName, Long jurisGroupId);

    /**
     * 通过分组id查询是否绑定单位
     *
     * @param jurisGroupId
     * @author wxc
     * @date 2021/8/9 16:08
     */
    List<JurisGroupDTO> findBusinessByJurisGroupId(Long jurisGroupId);
}
