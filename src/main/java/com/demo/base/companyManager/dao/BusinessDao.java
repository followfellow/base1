package com.demo.base.companyManager.dao;

import com.demo.action.vo.QueryPage;
import com.demo.base.companyManager.dto.BusinessDTO;
import com.demo.base.companyManager.request.FindBusinessParam;
import com.demo.base.companyManager.request.FindBusinessSelectParam;
import com.demo.base.companyManager.request.FindMerchantUserParam;
import com.demo.base.jurisGroupManager.request.FindGroupBusinessParam;
import com.demo.dbutils.BaseDAO;

import java.util.List;

/**
 * 单位管理dao
 *
 * @author:wxc
 * @date:2021/7/22 15:55
 */
public interface BusinessDao extends BaseDAO {
    /**
     * 分页查询单位列表
     *
     * @param businessDTO
     * @param queryPage
     * @author wxc
     * @date 2021/7/23 10:25
     */
    List<BusinessDTO> findBusinessList(FindBusinessParam businessDTO, QueryPage queryPage);

    /**
     * 查询运营商
     *
     * @param
     * @author wxc
     * @date 2021/7/24 9:31
     */
    BusinessDTO findOperation();

    /**
     * 通过全名和id查询
     *
     * @param businessAllName
     * @param businessId
     * @author wxc
     * @date 2021/7/24 14:13
     */
    List<BusinessDTO> findBusinessByAllNameAndId(String businessAllName, Long businessId);

    /**
     * 通过简称和id查询
     *
     * @param businessShortName
     * @param businessId
     * @author wxc
     * @date 2021/7/24 14:13
     */
    List<BusinessDTO> findBusinessByShortNameAndId(String businessShortName, Long businessId);

    /**
     * 查询分组下的单位
     *
     * @param findGroupBusinessParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/29 9:52
     */
    List<BusinessDTO> findGroupBusiness(FindGroupBusinessParam findGroupBusinessParam, QueryPage queryPage);

    /**
     * 查询未分组单位
     *
     * @param findGroupBusinessParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/29 9:52
     */
    List<BusinessDTO> findNeverInGroupBusiness(FindGroupBusinessParam findGroupBusinessParam, QueryPage queryPage);

    /**
     * 查询商家用户列表
     *
     * @param findMerchantUserParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/29 14:00
     */
    List<BusinessDTO> findBusinessUserList(FindMerchantUserParam findMerchantUserParam, QueryPage queryPage);

    /**
     * 查询商家用户详情
     *
     * @param businessId
     * @author wxc
     * @date 2021/7/29 14:01
     */
    BusinessDTO findBusinessUserInfo(Long businessId);

    /**
     * 获取单位下拉框
     *
     * @param
     * @param findBusinessSelectParam
     * @author wxc
     * @date 2021/8/2 14:24
     */
    List<BusinessDTO> findBusinessSelect(FindBusinessSelectParam findBusinessSelectParam);
}
