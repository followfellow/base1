package com.demo.base.cityManager.dao;

import com.demo.base.cityManager.dto.CityDTO;
import com.demo.base.cityManager.request.FindCityParam;
import com.demo.dbutils.BaseDAO;

import java.util.List;

/**
 * 城市DAO
 *
 * @author kj
 * @date 2021/8/12 18:32
 */
public interface CityDao extends BaseDAO {
    /*
     * 查询城市列表
     * @author kj
     * @date 2021/8/13 11:51  
     * @param [findCityParam]
     * @return java.util.List<com.demo.base.cityManager.dto.CityDTO>
     */
    List<CityDTO> findCityList(FindCityParam findCityParam);

    /*
     * 通过城市名称查找城市
     * @author kj
     * @date 2021/8/13 11:51  
     * @param [cityName, cityId]
     * @return java.util.List<cityDTO>
     */
    List<CityDTO> findcityByName(String cityName, Long cityId);

    List<CityDTO> findCitySelect(FindCityParam findCityParam);
}
