package com.demo.base.jurisGroupManager.service;

import cn.hutool.core.collection.CollectionUtil;
import com.demo.action.service.BaseServiceImpl;
import com.demo.base.jurisGroupManager.dao.JurisGroupDao;
import com.demo.base.jurisGroupManager.dto.JurisGroupDTO;
import com.demo.base.jurisGroupManager.po.JurisGroupDO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

/**
 * serviceImpl
 *
 * @author:wxc
 * @date:2021-07-23 17:03:23
 */
@Service
public class JurisGroupServiceImpl extends BaseServiceImpl implements JurisGroupService {
    @Resource
    private JurisGroupDao jurisGroupDao;

    /**
     * 添加
     *
     * @param jurisGroupDO
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void addJurisGroup(JurisGroupDO jurisGroupDO) {
        jurisGroupDao.save(jurisGroupDO);
    }

    /**
     * 通过id查询
     *
     * @param jurisGroupId
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    @Override
    public JurisGroupDO queryJurisGroupById(Long jurisGroupId) {
        return (JurisGroupDO) jurisGroupDao.getEntityById(JurisGroupDO.class, jurisGroupId);
    }

    /**
     * 查询列表
     *
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    @Override
    public List<JurisGroupDTO> findJurisGroupList() {
        return jurisGroupDao.findJurisGroupList();
    }

    /**
     * 更新
     *
     * @param jurisGroupDO
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateJurisGroup(JurisGroupDO jurisGroupDO) {
        jurisGroupDao.update(jurisGroupDO);
    }

    /**
     * 删除
     *
     * @param jurisGroupId
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteJurisGroup(Long jurisGroupId) {
        String sql = " delete from sys_juris_group_t where jurisGroupId = " + jurisGroupId;
        jurisGroupDao.executeSql(sql);
        sql = " delete from sys_juris_group_menu_t where jurisGroupId = " + jurisGroupId;
        jurisGroupDao.executeSql(sql);
    }

    /**
     * 检查是否存在下级分组
     *
     * @param jurisGroupId
     * @author wxc
     * @date 2021/7/24 14:57
     */
    @Override
    public boolean checkChildGroupIfExist(Long jurisGroupId) {
        return CollectionUtil.isNotEmpty(jurisGroupDao.findJurisGroupByPid(jurisGroupId));
    }

    /**
     * 通过单位id查询其分组
     *
     * @param businessId
     * @author wxc
     * @date 2021/7/26 15:10
     */
    @Override
    public JurisGroupDTO findGroupByBusinessId(Long businessId) {
        return jurisGroupDao.findGroupByBusinessId(businessId);
    }

    /**
     * 校验分组名称是否存在
     *
     * @param jurisGroupName
     * @param jurisGroupId
     * @author wxc
     * @date 2021/8/9 15:40
     */
    @Override
    public boolean checkNameIfExist(String jurisGroupName, Long jurisGroupId) {
        return CollectionUtil.isNotEmpty(jurisGroupDao.findJurisGroupByName(jurisGroupName, jurisGroupId));
    }

    /**
     * 校验分组是否分配单位
     *
     * @param jurisGroupId
     * @author wxc
     * @date 2021/8/9 16:07
     */
    @Override
    public boolean checkBusinessIfExist(Long jurisGroupId) {
        return CollectionUtil.isNotEmpty(jurisGroupDao.findBusinessByJurisGroupId(jurisGroupId));
    }
}
