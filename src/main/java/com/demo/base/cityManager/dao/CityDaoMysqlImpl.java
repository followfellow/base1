package com.demo.base.cityManager.dao;

import com.demo.base.cityManager.dto.CityDTO;
import com.demo.base.cityManager.request.FindCityParam;
import com.demo.contants.Constants;
import com.demo.dbutils.BaseDAOHibernateImpl;
import com.demo.utils.StringUtils;
import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 城市DAO
 *
 * @author kj
 * @date 2021/8/12 18:32
 */
@Repository(Constants.MYSQL + "CityDao")
public class CityDaoMysqlImpl extends BaseDAOHibernateImpl implements CityDao {
    /*
     * 查询城市列表
     * @author kj
     * @date 2021/8/13 11:54
     * @param [findCityParam]
     * @return java.util.List<com.demo.base.cityManager.dto.CityDTO>
     */
    @Override
    public List<CityDTO> findCityList(FindCityParam findCityParam) {
        String sql = "select cityId,cityName,cityChar,provinceId,certificateNo from pub_city_t where 1 = 1";
        if (findCityParam != null) {
            if (!StringUtils.isEmpty(findCityParam.getProvinceId())) {
                sql += " and provinceId = '" + findCityParam.getProvinceId() + "' ";
            }
        }
        return findObjectBySql(sql, CityDTO.class);
    }

    /*
     * 通过城市名称查找城市
     * @author kj
     * @date 2021/8/13 11:54
     * @param [cityName, cityId]
     * @return java.util.List<com.demo.base.cityManager.dto.CityDTO>
     */
    @Override
    public List<CityDTO> findcityByName(String cityName, Long cityId) {
        String sql = " select  cityId from  pub_city_t  where cityName = '" + StringEscapeUtils.escapeSql(cityName) + "' ";
        if (cityId != null) {
            sql += " and cityId <> " + cityId;
        }
//        sql += " and businessId = " + getCurrUserOrgId();
        sql += " limit 1  ";
        return findObjectBySql(sql, CityDTO.class);
    }

    @Override
    public List<CityDTO> findCitySelect(FindCityParam findCityParam) {
        String sql = "select cityId,cityName from pub_city_t where 1 = 1";
        if (findCityParam != null) {
            if (!StringUtils.isEmpty(findCityParam.getProvinceId())) {
                sql += " and provinceId = '" + findCityParam.getProvinceId() + "' ";
            }
        }
        return findObjectBySql(sql, CityDTO.class);
    }
}
