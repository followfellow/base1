package com.demo.base.provinceManager.dao;

import com.demo.base.provinceManager.dto.ProvinceDTO;
import com.demo.base.provinceManager.request.FindAreaTreeParam;
import com.demo.base.provinceManager.request.FindProvinceParam;
import com.demo.base.provinceManager.response.FindAreaResult;
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
        String sql = "select provinceId, provinceName, certificateNo" +
                " from pub_province_t  where 1 =  1";
        if (findProvinceParam.getProvinceId() != null) {
            sql += " and provinceId = " + findProvinceParam.getProvinceId();
        }
//        sql += " order by updatedDate desc ";
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
    public List<FindAreaResult> findProvinceNode(FindAreaTreeParam findAreaTreeParam) {
        String sql = "select provinceId id, provinceName title, certificateNo no"+
                " from pub_province_t where 1 = 1";
        return findObjectBySql(sql, FindAreaResult.class);
    }

    @Override
    public List<FindAreaResult> findCityNode(FindAreaTreeParam findAreaTreeParam) {
        String sql = "select cityId id, cityName title, provinceId pid, certificateNo no"+
                " from pub_city_t where 1 = 1";
        return findObjectBySql(sql, FindAreaResult.class);
    }

    @Override
    public List<FindAreaResult> findDistrictNode(FindAreaTreeParam findAreaTreeParam) {
        String sql = "select districtId id, districtName title, cityId pid, certificateNo no"+
                " from pub_district_t where 1 = 1";
        return findObjectBySql(sql, FindAreaResult.class);
    }

    @Override
    public List<ProvinceDTO> findProvinceSelect(FindProvinceParam findProvinceParam) {
        String sql = "select provinceId, provinceName" +
                " from pub_province_t  where 1 =  1";
        return findObjectBySql(sql, ProvinceDTO.class);
    }

}
