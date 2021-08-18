package com.demo.base.jurisGroupManager.service;

import com.demo.base.jurisGroupManager.dto.JurisGroupDTO;
import com.demo.base.jurisGroupManager.po.JurisGroupDO;

import java.util.List;


/**
 * service
 *
 * @author:wxc
 * @date:2021-07-23 17:03:23
 */
public interface JurisGroupService {


    /**
     * 添加
     *
     * @param jurisGroupDO
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    void addJurisGroup(JurisGroupDO jurisGroupDO);

    /**
     * 通过id查询
     *
     * @param jurisGroupId
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    JurisGroupDO queryJurisGroupById(Long jurisGroupId);

    /**
     * 查询列表
     *
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    List<JurisGroupDTO> findJurisGroupList();

    /**
     * 更新
     *
     * @param jurisGroupDO
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    void updateJurisGroup(JurisGroupDO jurisGroupDO);

    /**
     * 删除单位
     *
     * @param jurisGroupId
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    void deleteJurisGroup(Long jurisGroupId);

    /**
     * 检查是否存在下级分组
     *
     * @param jurisGroupId
     * @author wxc
     * @date 2021/7/24 14:57
     */
    boolean checkChildGroupIfExist(Long jurisGroupId);

    /**
     * 通过单位id查询其分组
     *
     * @param businessId
     * @author wxc
     * @date 2021/7/26 15:10
     */
    JurisGroupDTO findGroupByBusinessId(Long businessId);

    /**
     * 校验分组名称是否存在
     *
     * @param jurisGroupName
     * @param jurisGroupId
     * @author wxc
     * @date 2021/8/9 15:40
     */
    boolean checkNameIfExist(String jurisGroupName, Long jurisGroupId);

    /**
     * 校验分组是否分配单位
     *
     * @param jurisGroupId
     * @author wxc
     * @date 2021/8/9 16:07
     */
    boolean checkBusinessIfExist(Long jurisGroupId);
}
