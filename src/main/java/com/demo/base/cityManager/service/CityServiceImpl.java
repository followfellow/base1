package com.demo.base.cityManager.service;

import cn.hutool.core.collection.CollectionUtil;
import com.demo.action.service.BaseServiceImpl;
import com.demo.action.vo.QueryPage;
import com.demo.base.cityManager.dao.CityDao;
import com.demo.base.cityManager.dto.CityDTO;
import com.demo.base.cityManager.po.CityDO;
import com.demo.base.cityManager.request.FindCityParam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

/**
 * 城市Service
 *
 * @author kj
 * @date 2021/8/12 18:34
 */
@Service
public class CityServiceImpl extends BaseServiceImpl implements CityService {

    @Resource(name = "${sql}" + "CityDao")

    private CityDao cityDao;

    /*
     * 查询城市列表
     * @author kj
     * @date 2021/8/13 11:41  
     * @param [findCityParam]
     * @return java.util.List<com.demo.base.cityManager.dto.CityDTO>
     */
    @Override
    public List<CityDTO> findCityList(FindCityParam findCityParam, QueryPage queryPage) {
        return cityDao.findCityList(findCityParam,queryPage);
    }

    /*
     * 添加城市
     * @author kj
     * @date 2021/8/13 11:41  
     * @param [cityDO]
     * @return void
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void addCity(CityDO cityDO) {
        cityDao.save(cityDO);
    }

    /*
     * 校验城市名是否存在
     * @author kj
     * @date 2021/8/13 11:41  
     * @param [cityName, cityId]
     * @return boolean
     */
    @Override
    public boolean checkNameIfExist(String cityName, Long cityId) {
        return CollectionUtil.isNotEmpty(cityDao.findcityByName(cityName, cityId));
    }

    /*
     * 根据 id 查询 城市
     * @author kj
     * @date 2021/8/13 11:41  
     * @param [cityId]
     * @return com.demo.base.cityManager.po.CityDO
     */
    @Override
    public CityDO queryCityById(Long cityId) {
        return (CityDO) cityDao.getEntityById(CityDO.class, cityId);
    }

    /*
     * 修改城市
     * @author kj
     * @date 2021/8/13 11:41  
     * @param [cityDO]
     * @return void
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateCity(CityDO cityDO) {
        cityDao.update(cityDO);
    }

    /*
     * 根据id 删除 城市
     * @author kj
     * @date 2021/8/13 11:41  
     * @param [cityId]
     * @return void
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteCity(Long cityId) {
        String sql = "delete from pub_city_t where cityId = " + cityId;
        cityDao.executeSql(sql);
    }
}
