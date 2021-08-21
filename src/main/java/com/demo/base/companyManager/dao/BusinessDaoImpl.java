package com.demo.base.companyManager.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.demo.action.vo.QueryPage;
import com.demo.base.companyManager.dto.BusinessDTO;
import com.demo.base.companyManager.request.FindBusinessParam;
import com.demo.base.companyManager.request.FindBusinessSelectParam;
import com.demo.base.companyManager.request.FindMerchantUserParam;
import com.demo.base.jurisGroupManager.request.FindGroupBusinessParam;
import com.demo.dbutils.BaseDAOHibernateImpl;
import com.demo.utils.StringUtils;
import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 单位daoimpl
 *
 * @author:wxc
 * @date:2021/7/22 15:55
 */
@Repository
public class BusinessDaoImpl extends BaseDAOHibernateImpl implements BusinessDao {
    /**
     * 分页查询单位列表
     *
     * @param findBusinessParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/23 10:25
     */
    @Override
    public List<BusinessDTO> findBusinessList(FindBusinessParam findBusinessParam, QueryPage queryPage) {
        String sql = "select businessId, businessAllName ,businessShortName,businessType,areaNameStr,contactName,contactPhoneNo,flag,validTime, destinationType,websiteName,websiteAddress" +
                " from org_business_t where 1 = 1 ";
        if (findBusinessParam.getBusinessId() != null) {
            sql += " and businessId =  " + findBusinessParam.getBusinessId();
        }
        if (findBusinessParam.getDestinationType() != null) {
            sql += " and destinationType =  " + findBusinessParam.getDestinationType();
        }
        if (findBusinessParam.getBusinessType() != null) {
            sql += " and businessType =  " + findBusinessParam.getBusinessType();
        }
        sql += " order by updatedDate desc";
        return findObjectBySql(sql, BusinessDTO.class, queryPage);
    }

    /**
     * 查询运营商
     *
     * @author wxc
     * @date 2021/7/24 9:31
     */
    @Override
    public BusinessDTO findOperation() {
        String sql = "select businessId, businessAllName ,businessShortName,businessType,contactName,contactPhoneNo, platformName,platformLogo,mallName,mallLogo,userName" +
                " from org_business_t where businessType = 0 limit 1 ";
        List<BusinessDTO> list = findObjectBySql(sql, BusinessDTO.class);
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 通过全名和id查询
     *
     * @param businessAllName
     * @param businessId
     * @author wxc
     * @date 2021/7/24 14:13
     */
    @Override
    public List<BusinessDTO> findBusinessByAllNameAndId(String businessAllName, Long businessId) {
        String sql = "select businessId" +
                " from org_business_t where businessAllName =  '" + StringEscapeUtils.escapeSql(businessAllName) + "' ";
        if (businessId != null) {
            sql += " and businessId <> " + businessId;
        }
        sql+=" limit 1";
        return findObjectBySql(sql, BusinessDTO.class);
    }

    /**
     * 通过简称和id查询
     *
     * @param businessShortName
     * @param businessId
     * @author wxc
     * @date 2021/7/24 14:13
     */
    @Override
    public List<BusinessDTO> findBusinessByShortNameAndId(String businessShortName, Long businessId) {
        String sql = "select businessId" +
                " from org_business_t where businessShortName =  '" + StringEscapeUtils.escapeSql(businessShortName) + "' ";
        if (businessId != null) {
            sql += " and businessId <> " + businessId;
        }
        sql+=" limit 1";
        return findObjectBySql(sql, BusinessDTO.class);
    }

    /**
     * 查询分组下的单位
     *
     * @param findGroupBusinessParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/29 9:52
     */
    @Override
    public List<BusinessDTO> findGroupBusiness(FindGroupBusinessParam findGroupBusinessParam, QueryPage queryPage) {
        String sql = "select t1.businessId, t1.businessAllName ,t1.businessShortName,t1.businessType,t1.areaNameStr,t1.contactName, t1.destinationType" +
                " from org_business_t t1, sys_juris_group_org_t t2" +
                " where t1.businessId = t2.businessId ";
        sql += " and t2.jurisGroupId =  " + findGroupBusinessParam.getJurisGroupId();
        if (findGroupBusinessParam.getBusinessType() != null) {
            sql += " and t1.businessType =  " + findGroupBusinessParam.getBusinessType();
        }
        if (findGroupBusinessParam.getDestinationType() != null) {
            sql += " and t1.destinationType =  " + findGroupBusinessParam.getDestinationType();
        }
        if (StringUtils.isNotBlank(findGroupBusinessParam.getBusinessAllName())) {
            sql += " and t1.businessAllName like  '%" + StringEscapeUtils.escapeSql(findGroupBusinessParam.getBusinessAllName()) + "%'";
        }
        sql += " order by t1.updatedDate desc";
        return findObjectBySql(sql, BusinessDTO.class, queryPage);
    }

    /**
     * 查询未分组单位
     *
     * @param findGroupBusinessParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/29 9:52
     */
    @Override
    public List<BusinessDTO> findNeverInGroupBusiness(FindGroupBusinessParam findGroupBusinessParam, QueryPage queryPage) {
        String sql = "select t1.businessId, t1.businessAllName ,t1.businessShortName,t1.businessType,t1.areaNameStr,t1.contactName ,t1.destinationType" +
                " from org_business_t t1" +
                " where 1 = 1 ";
        if (findGroupBusinessParam.getBusinessType() != null) {
            sql += " and t1.businessType =  " + findGroupBusinessParam.getBusinessType();
        }
        if (findGroupBusinessParam.getDestinationType() != null) {
            sql += " and t1.destinationType =  " + findGroupBusinessParam.getDestinationType();
        }
        if (StringUtils.isNotBlank(findGroupBusinessParam.getBusinessAllName())) {
            sql += " and t1.businessAllName like  '%" + StringEscapeUtils.escapeSql(findGroupBusinessParam.getBusinessAllName()) + "%'";
        }
        sql += " and not exists ( select t2.jurisGroupOrgId from sys_juris_group_org_t t2 where t1.businessId = t2.businessId  )";
        sql += " order by t1.updatedDate desc";
        return findObjectBySql(sql, BusinessDTO.class, queryPage);
    }

    /**
     * 查询商家用户列表
     *
     * @param findMerchantUserParam
     * @param queryPage
     * @author wxc
     * @date 2021/7/29 14:00
     */
    @Override
    public List<BusinessDTO> findBusinessUserList(FindMerchantUserParam findMerchantUserParam, QueryPage queryPage) {
        String sql = "select t1.businessId, t1.businessAllName ,t1.businessShortName,t1.businessType,t1.contactName,t1.contactPhoneNo, t1.userName,t1.flag,t1.validTime,t3.jurisGroupName" +
                " from org_business_t t1" +
                " left join sys_juris_group_org_t t2 on t1.businessId = t2.businessId " +
                " left join sys_juris_group_t t3 on t2.jurisGroupId = t3.jurisGroupId " +
                " where 1 = 1 and t1.businessType <> 0";
        if (findMerchantUserParam.getBusinessType() != null) {
            sql += " and t1.businessType =  " + findMerchantUserParam.getBusinessType();
        }
        if (StringUtils.isNotBlank(findMerchantUserParam.getCondition())) {
            String condition = StringEscapeUtils.escapeSql(findMerchantUserParam.getCondition());
            sql += "  and (userName like '%" + condition + "%' or" +
                    " businessAllName like '%" + condition + "%' or " +
                    " contactName like '%" + condition + "%' or " +
                    " cellphone = '" + condition + "' )";
        }
        return findObjectBySql(sql, BusinessDTO.class, queryPage);
    }

    /**
     * 查询商家用户详情
     *
     * @param businessId
     * @author wxc
     * @date 2021/7/29 14:01
     */
    @Override
    public BusinessDTO findBusinessUserInfo(Long businessId) {
        String sql = "select t1.businessId, t1.businessAllName ,t1.businessShortName,t1.businessType,t1.contactName, t1.userName,t1.flag,t1.validTime,t3.jurisGroupName" +
                " from org_business_t t1" +
                " left join sys_juris_group_org_t t2 on t1.businessId = t2.businessId " +
                " left join sys_juris_group_t t3 on t2.jurisGroupId = t3.jurisGroupId " +
                " where t1.businessId =  " + businessId+
                " limit 1";
        List<BusinessDTO> list = findObjectBySql(sql, BusinessDTO.class);
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 获取单位下拉框
     *
     * @param findBusinessSelectParam
     * @author wxc
     * @date 2021/8/2 14:24
     */
    @Override
    public List<BusinessDTO> findBusinessSelect(FindBusinessSelectParam findBusinessSelectParam) {
        String sql = "select businessId, businessAllName ,businessShortName " +
                " from org_business_t where businessType = " + findBusinessSelectParam.getBusinessType() + "  order by updatedDate desc";
        return findObjectBySql(sql, BusinessDTO.class);
    }
}
