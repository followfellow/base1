package com.demo.base.provinceManager.dao;

import com.demo.action.vo.QueryPage;
import com.demo.base.provinceManager.dto.ProvinceDTO;
import com.demo.base.provinceManager.request.FindProvinceParam;
import com.demo.dbutils.BaseDAOHibernateImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 省份daoImpl
 *
 * @author:kj
 * @date:2021-08-20 16:54
 */
@Repository
public class ProvinceDaoImpl extends BaseDAOHibernateImpl implements ProvinceDao {
    /**
     * 查询省份列表
     *
     * @param findProvinceParam
     * @param queryPage
     * @author kj
     * @date 2021-08-20 16:54
     */
    @Override
    public List<ProvinceDTO> findProvinceList(FindProvinceParam findProvinceParam, QueryPage queryPage) {
        String sql = "select provinceId, provinceName,provinceChar,certificateNo" +
                " from pub_province_t  where 1 =  1";
        if (findProvinceParam.getProvinceId() != null) {
            sql += " and provinceId = " + findProvinceParam.getProvinceId();
        }
        sql += " order by updatedDate desc ";
        return findObjectBySql(sql, ProvinceDTO.class, queryPage);
    }

}
