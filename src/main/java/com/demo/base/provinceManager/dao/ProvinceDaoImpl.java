package com.demo.base.provinceManager.dao;

import com.demo.base.provinceManager.dto.AreaDTO;
import com.demo.base.provinceManager.dto.ProvinceDTO;
import com.demo.base.provinceManager.request.FindAreaTreeParam;
import com.demo.base.provinceManager.request.FindProvinceParam;
import com.demo.dbutils.BaseDAOHibernateImpl;
import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
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
     * @author kj
     * @date 2021-08-20 16:54
     */
    @Override
    public List<ProvinceDTO> findProvinceList(FindProvinceParam findProvinceParam) {
        String sql = "select provinceId, provinceName,provinceChar,certificateNo" +
                " from pub_province_t  where 1 =  1";
        if (findProvinceParam.getProvinceId() != null) {
            sql += " and provinceId = " + findProvinceParam.getProvinceId();
        }
        sql += " order by updatedDate desc ";
        return findObjectBySql(sql, ProvinceDTO.class);
    }

    @Override
    public List<ProvinceDTO> findProvinceByName(String provinceName, Long provinceId) {
        String sql = " select  provinceId from  pub_province_t  where provinceName = '" + StringEscapeUtils.escapeSql(provinceName) + "' ";
        if (provinceId != null) {
            sql += " and provinceId <> " + provinceId;
        }
        sql += " limit 1  ";
        return findObjectBySql(sql, ProvinceDTO.class);
    }

    @Override
    public List<AreaDTO> findAreaList(FindAreaTreeParam findAreaTreeParam) {
        String sql = " SELECT t1.provinceId,t1.provinceName " +
                " ,t2.cityId,t2.cityName " +
                " ,t3.districtId,t3.districtName " +
                " FROM pub_province_t t1 " +
                " LEFT JOIN pub_city_t t2 ON t1.provinceId = t2.provinceId " +
                " LEFT JOIN pub_district_t t3 ON t2.cityId = t3.cityId " +
                " WHERE 1 = 1 ";
        if (findAreaTreeParam.getProvinceId()!=null){
            sql += " AND t1.provinceId = " + findAreaTreeParam.getProvinceId();
        }
        if (findAreaTreeParam.getCityId()!=null){
            sql += " AND t2.cityId = " + findAreaTreeParam.getCityId();
        }
        if (findAreaTreeParam.getDistrictId()!=null){
            sql += " AND t3.districtId = " + findAreaTreeParam.getDistrictId();
        }


        return findObjectBySql(sql, AreaDTO.class);
    }

}
