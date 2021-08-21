package com.demo.base.companyManager.service;

import com.demo.action.vo.QueryPage;
import com.demo.base.companyManager.dto.BusinessDTO;
import com.demo.base.companyManager.po.BusinessDO;
import com.demo.base.companyManager.po.JurisGroupOrgDO;
import com.demo.base.companyManager.request.*;
import com.demo.base.jurisGroupManager.request.FindGroupBusinessParam;
import com.demo.base.roleManager.po.RoleDO;
import com.demo.base.userManager.po.UserDO;
import com.demo.dbutils.BaseApplicationDO;

import java.util.List;

/**
 * 单位service
 *
 * @author:wxc
 * @date:2021/7/22 15:56
 */
public interface BusinessService {


    /**
     * 添加单位
     *
     * @param baseApplicationDOList
     * @author wxc
     * @date 2021/7/23 10:09
     */
    void addBusiness(List<BaseApplicationDO> baseApplicationDOList);

    /**
     * 通过id查询单位
     *
     * @param businessId
     * @author wxc
     * @date 2021/7/23 10:10
     */
    BusinessDO queryBusinessById(Long businessId);

    /**
     * 查询单位列表
     *
     * @param businessDTO
     * @param queryPage
     * @author wxc
     * @date 2021/7/23 10:10
     */
    List<BusinessDTO> findBusinessList(FindBusinessParam businessDTO, QueryPage queryPage);

    /**
     * 更新单位信息
     *
     * @param businessDO
     * @author wxc
     * @date 2021/7/23 10:10
     */
    void updateBusiness(BusinessDO businessDO);

    /**
     * 删除单位
     *
     * @param businessId
     * @author wxc
     * @date 2021/7/23 10:11
     */
    void deleteBusiness(Long businessId);

    /**
     * 查询运营商
     *
     * @param
     * @author wxc
     * @date 2021/7/24 9:30
     */
    BusinessDTO findOperation();

    /**
     * 重置密码
     *
     * @param resetPasswordParam
     * @author wxc
     * @date 2021/7/24 9:48
     */
    void resetPassword(ResetPasswordParam resetPasswordParam);

    /**
     * 修改状态
     *
     * @param modifyStatusParam
     * @author wxc
     * @date 2021/7/24 9:59
     */
    void modifyStatus(ModifyStatusParam modifyStatusParam);

    /**
     * 校验单位全称是否存在
     *
     * @param businessAllName
     * @param businessId
     * @author wxc
     * @date 2021/7/24 14:09
     */
    boolean checkAllNameIfExist(String businessAllName, Long businessId);

    /**
     * 校验单位简称是否存在
     *
     * @param businessShortName
     * @param businessId
     * @author wxc
     * @date 2021/7/24 14:10
     */
    boolean checkShortNameIfExist(String businessShortName, Long businessId);

    /**
     * 增加分组单位
     *
     * @param jurisGroupOrgDOList
     * @author wxc
     * @date 2021/7/29 9:40
     */
    void operateGroupBusiness(List<JurisGroupOrgDO> jurisGroupOrgDOList);

    /**
     * 查询分组单位
     *
     * @param findGroupBusinessParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/29 9:40
     */
    List<BusinessDTO> findGroupBusiness(FindGroupBusinessParam findGroupBusinessParam, QueryPage queryPage);

    /**
     * 删除分组单位
     *
     * @param deleteGroupBusinessParam
     * @author wxc
     * @date 2021/7/29 9:40
     */
    void deleteGroupBusiness(DeleteGroupBusinessParam deleteGroupBusinessParam);

    /**
     * 查询未分组单位
     *
     * @param findGroupBusinessParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/29 9:41
     */
    List<BusinessDTO> findNeverInGroupBusiness(FindGroupBusinessParam findGroupBusinessParam, QueryPage queryPage);

    /**
     * 查询商家用户列表
     *
     * @param findMerchantUserParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/29 13:58
     */
    List<BusinessDTO> findBusinessUserList(FindMerchantUserParam findMerchantUserParam, QueryPage queryPage);

    /**
     * 查询商家用户详情
     *
     * @param businessId
     * @author wxc
     * @date 2021/7/29 13:58
     */
    BusinessDTO findBusinessUserInfo(Long businessId);

    /**
     * findBusinessSelect
     *
     * @param
     * @param findBusinessSelectParam
     * @author wxc
     * @date 2021/8/2 14:24
     */
    List<BusinessDTO> findBusinessSelect(FindBusinessSelectParam findBusinessSelectParam);

}
