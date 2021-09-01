package com.demo.cache.city;

import com.demo.base.cityManager.cache.FindCityCacheParam;
import com.demo.base.cityManager.dto.CityDTO;
import com.demo.dbutils.BaseDAOHibernateImpl;
import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author kj
 * @date 2021/8/23 18:45
 */
@Repository
public class CityRedisDaoImpl extends BaseDAOHibernateImpl implements CityRedisDao{
    @Override
    public List<CityDTO> findCityList(FindCityCacheParam findCityCacheParam) {
        String sql = " SELECT cityId,cityName,cityChar,certificateNo,provinceId from pub_city_t where 1 = 1 ";
        if (findCityCacheParam != null) {
            if (findCityCacheParam.getCityId()!= null) {
                sql += " and cityId = '" + StringEscapeUtils.escapeSql(findCityCacheParam.getCityId().toString()) + "' ";
            }
        }

        sql += " order by cityId ";
        return super.findObjectBySql(sql, CityDTO.class);
    }
}
