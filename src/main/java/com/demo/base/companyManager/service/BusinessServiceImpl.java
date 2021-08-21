package com.demo.base.companyManager.service;

import cn.hutool.core.collection.CollectionUtil;
import com.demo.action.service.BaseServiceImpl;
import com.demo.action.vo.QueryPage;
import com.demo.base.companyManager.dao.BusinessDao;
import com.demo.base.companyManager.dto.BusinessDTO;
import com.demo.base.companyManager.po.BusinessDO;
import com.demo.base.companyManager.po.JurisGroupOrgDO;
import com.demo.base.companyManager.request.*;
import com.demo.base.jurisGroupManager.request.FindGroupBusinessParam;
import com.demo.dbutils.BaseApplicationDO;
import com.demo.utils.StringUtils;
import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

/**
 * 单位serviceimpl
 *
 * @author:wxc
 * @date:2021/7/22 15:56
 */
@Service
public class BusinessServiceImpl extends BaseServiceImpl implements BusinessService {

    @Resource
    private BusinessDao businessDao;

    /**
     * 添加单位
     *
     * @param baseApplicationDOList
     * @author wxc
     * @date 2021/7/23 10:09
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void addBusiness(List<BaseApplicationDO> baseApplicationDOList) {
        if (CollectionUtil.isNotEmpty(baseApplicationDOList)) {
            baseApplicationDOList.forEach(baseApplicationDO -> businessDao.save(baseApplicationDO));
        }
    }

    /**
     * 通过id查询单位
     *
     * @param businessId
     * @author wxc
     * @date 2021/7/23 10:10
     */
    @Override
    public BusinessDO queryBusinessById(Long businessId) {
        return (BusinessDO) businessDao.getEntityById(BusinessDO.class, businessId);
    }

    /**
     * 查询单位列表
     *
     * @param findBusinessParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/23 10:10
     */
    @Override
    public List<BusinessDTO> findBusinessList(FindBusinessParam findBusinessParam, QueryPage queryPage) {
        return businessDao.findBusinessList(findBusinessParam, queryPage);
    }

    /**
     * 更新单位信息
     *
     * @param businessDO
     * @author wxc
     * @date 2021/7/23 10:10
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateBusiness(BusinessDO businessDO) {
        businessDao.update(businessDO);
        String sql = "update sys_user_t set userRealName = '" + businessDO.getContactName() + "' , " +
                //如果有效期有值则修改
                (StringUtils.isNotBlank(businessDO.getValidTime()) ? (" validTime = '" + businessDO.getValidTime() + "' , ") : "") +
                " cellphone = '" + businessDO.getContactPhoneNo() + "'  " +
                " where userName = '" + StringEscapeUtils.escapeSql(businessDO.getUserName()) + "' limit 1";
        businessDao.executeSql(sql);
    }

    /**
     * 删除单位
     *
     * @param businessId
     * @author wxc
     * @date 2021/7/23 10:11
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteBusiness(Long businessId) {
        //删除单位表
        String sql = "delete from org_business_t where businessId = " + businessId + " limit 1";
        businessDao.executeSql(sql);
        //删除单位分组绑定表
        sql = "delete from sys_juris_group_org_t where businessId = " + businessId;
        businessDao.executeSql(sql);
        //删除单位下的用户表
        sql = "delete from sys_user_t where businessId = " + businessId;
        businessDao.executeSql(sql);
        //删除单位下角色表
        sql = "delete from sys_role_t where businessId = " + businessId;
        businessDao.executeSql(sql);
        //删除单位下用户角色关联表
        sql = "delete from sys_role_join_user_t where businessId = " + businessId;
        businessDao.executeSql(sql);
        //删除单位下角色菜单权限关联表
        sql = "delete from sys_role_menu_t where businessId = " + businessId;
        businessDao.executeSql(sql);
    }

    /**
     * 查询运营商
     *
     * @author wxc
     * @date 2021/7/24 9:30
     */
    @Override
    public BusinessDTO findOperation() {
        return businessDao.findOperation();
    }

    /**
     * 重置密码
     *
     * @param resetPasswordParam
     * @author wxc
     * @date 2021/7/24 9:48
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void resetPassword(ResetPasswordParam resetPasswordParam) {
        String sql = "update sys_user_t set password = '" + resetPasswordParam.getPassword() + "' where userName = '" + StringEscapeUtils.escapeSql(resetPasswordParam.getUserName()) + "' limit 1";
        businessDao.executeSql(sql);
    }

    /**
     * 修改状态
     *
     * @param modifyStatusParam
     * @author wxc
     * @date 2021/7/24 9:59
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void modifyStatus(ModifyStatusParam modifyStatusParam) {
        String sql = "update sys_user_t set flag = " + modifyStatusParam.getFlag() + " where userName = '" + StringEscapeUtils.escapeSql(modifyStatusParam.getUserName()) + "' limit 1";
        businessDao.executeSql(sql);
        sql = "update org_business_t set flag = " + modifyStatusParam.getFlag() + " where businessId = " + modifyStatusParam.getBusinessId() + " limit 1";
        businessDao.executeSql(sql);
    }

    /**
     * 校验单位全称是否存在
     *
     * @param businessAllName
     * @param businessId
     * @author wxc
     * @date 2021/7/24 14:09
     */
    @Override
    public boolean checkAllNameIfExist(String businessAllName, Long businessId) {
        return CollectionUtil.isNotEmpty(businessDao.findBusinessByAllNameAndId(businessAllName, businessId));
    }

    /**
     * 校验单位简称是否存在
     *
     * @param businessShortName
     * @param businessId
     * @author wxc
     * @date 2021/7/24 14:10
     */
    @Override
    public boolean checkShortNameIfExist(String businessShortName, Long businessId) {
        return CollectionUtil.isNotEmpty(businessDao.findBusinessByShortNameAndId(businessShortName, businessId));
    }

    /**
     * 增加分组单位
     *
     * @param jurisGroupOrgDOList
     * @author wxc
     * @date 2021/7/29 9:40
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void operateGroupBusiness(List<JurisGroupOrgDO> jurisGroupOrgDOList) {
        if (CollectionUtil.isNotEmpty(jurisGroupOrgDOList)) {
            jurisGroupOrgDOList.forEach(jurisGroupOrgDO -> businessDao.save(jurisGroupOrgDO));
        }
    }

    /**
     * 查询分组单位
     *
     * @param findGroupBusinessParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/29 9:40
     */
    @Override
    public List<BusinessDTO> findGroupBusiness(FindGroupBusinessParam findGroupBusinessParam, QueryPage queryPage) {
        return businessDao.findGroupBusiness(findGroupBusinessParam, queryPage);
    }

    /**
     * 删除分组单位
     *
     * @param deleteGroupBusinessParam
     * @author wxc
     * @date 2021/7/29 9:40
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteGroupBusiness(DeleteGroupBusinessParam deleteGroupBusinessParam) {
        String sql = "delete from  sys_juris_group_org_t where businessId = " + deleteGroupBusinessParam.getBusinessId() + " and jurisGroupId = " + deleteGroupBusinessParam.getJurisGroupId();
        businessDao.executeSql(sql);
    }

    /**
     * 查询未分组单位
     *
     * @param findGroupBusinessParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/29 9:41
     */
    @Override
    public List<BusinessDTO> findNeverInGroupBusiness(FindGroupBusinessParam findGroupBusinessParam, QueryPage queryPage) {
        return businessDao.findNeverInGroupBusiness(findGroupBusinessParam, queryPage);
    }

    /**
     * 查询商家用户列表
     *
     * @param findBusinessParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/29 13:58
     */
    @Override
    public List<BusinessDTO> findBusinessUserList(FindMerchantUserParam findBusinessParam, QueryPage queryPage) {
        return businessDao.findBusinessUserList(findBusinessParam, queryPage);
    }

    /**
     * 查询商家用户详情
     *
     * @param businessId
     * @author wxc
     * @date 2021/7/29 13:58
     */
    @Override
    public BusinessDTO findBusinessUserInfo(Long businessId) {
        return businessDao.findBusinessUserInfo(businessId);
    }

    /**
     * findBusinessSelect
     *
     * @author wxc
     * @date 2021/8/2 14:24
     * @param findBusinessSelectParam
     */
    @Override
    public List<BusinessDTO> findBusinessSelect(FindBusinessSelectParam findBusinessSelectParam) {
        return businessDao.findBusinessSelect(findBusinessSelectParam);
    }
}
