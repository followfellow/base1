package com.demo.cache.city;

import com.demo.base.cityManager.cache.FindCityCacheParam;
import com.demo.base.cityManager.dto.CityDTO;
import com.demo.dbutils.BaseDAO;

import java.util.List;

/**
 * @author kj
 * @date 2021/8/23 18:44
 */
public interface CityRedisDao extends BaseDAO {
    List<CityDTO> findCityList(FindCityCacheParam findCityCacheParam);
}
