package com.demo.base.cityManager.service;

import com.demo.action.vo.QueryPage;
import com.demo.base.cityManager.dto.CityDTO;
import com.demo.base.cityManager.po.CityDO;
import com.demo.base.cityManager.request.FindCityParam;

import java.util.List;

/**
 * 城市Service
 *
 * @author kj
 * @date 2021/8/12 18:34
 */
public interface CityService {
    /*
     * 查询城市列表
     * @author kj
     * @date 2021/8/13 11:39  
     * @param [findCityParam]
     * @return java.util.List<com.demo.base.cityManager.dto.CityDTO>
     */
    List<CityDTO> findCityList(FindCityParam findCityParam, QueryPage queryPage);

    /*
     * 添加城市
     * @author kj
     * @date 2021/8/13 11:39  
     * @param [cityDO]
     * @return void
     */
    void addCity(CityDO cityDO);

    /*
     * 校验城市名是否存在
     * @author kj
     * @date 2021/8/13 11:39  
     * @param [cityName, cityId]
     * @return boolean
     */
    boolean checkNameIfExist(String cityName, Long cityId);

    /*
     * 根据 id 查询 城市
     * @author kj
     * @date 2021/8/13 11:39  
     * @param [cityId]
     * @return com.demo.base.cityManager.po.CityDO
     */
    CityDO queryCityById(Long cityId);

    /*
     * 修改城市
     * @author kj
     * @date 2021/8/13 11:39  
     * @param [cityDO]
     * @return void
     */
    void updateCity(CityDO cityDO);

    /*
     * 根据id 删除 城市
     * @author kj
     * @date 2021/8/13 11:40  
     * @param [cityId]
     * @return void
     */
    void deleteCity(Long cityId);
}
