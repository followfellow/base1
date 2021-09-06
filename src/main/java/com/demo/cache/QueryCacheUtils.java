package com.demo.cache;

import com.demo.base.cityManager.cache.CityCacheDTO;
import com.demo.base.countryManager.cache.CountryCacheDTO;
import com.demo.base.districtManager.cache.DistrictCacheDTO;
import com.demo.base.provinceManager.cache.ProvinceCacheDTO;
import com.demo.contants.RedisConstants;
import com.demo.redis.RedisUtils;
import com.demo.system.codeManager.dto.CodeDTO;
import com.demo.system.propertyManager.dto.PropertyDTO;
import com.demo.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 从缓存中获取基础信息
 *
 * @author KJ
 */
@Component
@Slf4j
public class QueryCacheUtils {

    @Autowired
    private RedisUtils redisUtils;


    /*
     * 查找国家列表
     * @author kj
     * @date 2021/8/23 19:31
     * @param []
     * @return java.util.List<com.demo.base.countryManager.dto.CountryDTO>
     */
    public List<CountryCacheDTO> findCacheCountryList() {
        List<Object> objectList = redisUtils.findAllByKey(RedisConstants.COUNTRY);
        if (objectList != null || !objectList.isEmpty()) {
            return (List<CountryCacheDTO>) (List) objectList;
        }
        return null;
    }


    /*
     * 查找省份列表
     * @author kj
     * @date 2021/8/23 19:31
     * @param []
     * @return java.util.List<com.demo.base.provinceManager.dto.ProvinceDTO>
     */
    public List<ProvinceCacheDTO> findCacheProvinceList() {
        List<Object> objectList = redisUtils.findAllByKey(RedisConstants.PROVINCE);
        if (objectList != null || !objectList.isEmpty()) {
            return (List<ProvinceCacheDTO>) (List) objectList;
        }
        return null;
    }


    /*
     * 查找城市列表
     * @author kj
     * @date 2021/8/23 19:32
     * @param []
     * @return java.util.List<com.demo.base.cityManager.dto.CityDTO>
     */
    public List<CityCacheDTO> findCacheCityList() {
        List<Object> objectList = redisUtils.findAllByKey(RedisConstants.CITY);
        if (objectList != null || !objectList.isEmpty()) {
            return (List<CityCacheDTO>) (List) objectList;
        }
        return null;
    }


    /*
     * 根据省份id查询城市
     * @author kj
     * @date 2021/8/26 14:58
     * @param [provinceId]
     * @return java.util.List<com.demo.base.cityManager.cache.CityCacheDTO>
     */
    public List<CityCacheDTO> findCitiesByProvinceId(Long provinceId) {
        List<CityCacheDTO> cacheCityList = findCacheCityList();
        Map<Long, List<CityCacheDTO>> listMap = cacheCityList.stream().collect(Collectors.groupingBy(CityCacheDTO::getProvinceId));
        return listMap.get(provinceId);
    }

    /*
     * 查找地区列表
     * @author kj
     * @date 2021/8/23 19:32
     * @param []
     * @return java.util.List<com.demo.base.districtManager.dto.DistrictDTO>
     */
    public List<DistrictCacheDTO> findCacheDistrictList() {
        List<Object> objectList = redisUtils.findAllByKey(RedisConstants.DISTRICT);
        if (objectList != null || !objectList.isEmpty()) {
            return (List<DistrictCacheDTO>) (List) objectList;
        }
        return null;
    }

    /*
     * 根据城市id查找地区
     * @author kj
     * @date 2021/8/26 14:53
     * @param [cityId]
     * @return java.util.List<com.demo.base.districtManager.cache.DistrictCacheDTO>
     */
    public List<DistrictCacheDTO> findDistrictByCityId(Long cityId) {
        List<DistrictCacheDTO> cacheDistrictList = findCacheDistrictList();
        Map<Long, List<DistrictCacheDTO>> listMap = cacheDistrictList.stream().collect(Collectors.groupingBy(DistrictCacheDTO::getCityId));
        return listMap.get(cityId);
    }


    /*
     *
     * @author kj
     * @date 2021/8/31 16:52
     * @param [cityId]
     * @return java.lang.Long
     */
    public Long findProvinceIdByCityId(Long cityId) {
        Object object = redisUtils.getRedisByKey(RedisConstants.CITY, cityId.toString());
        CityCacheDTO cityCacheDTO = (CityCacheDTO) object;
        return cityCacheDTO.getProvinceId();
    }

    /*
     *
     * @author kj
     * @date 2021/8/31 16:52
     * @param [districtId]
     * @return java.lang.Long
     */
    public Long findCityIdByDistrictId(Long districtId) {
        Object object = redisUtils.getRedisByKey(RedisConstants.DISTRICT, districtId.toString());
        DistrictCacheDTO districtCacheDTO = (DistrictCacheDTO) object;
        return districtCacheDTO.getCityId();
    }

    /**
     * 根据参数键从缓存中获取系统参数信息
     *
     * @param: propKey
     * @author:zc
     * @date 2019/12/13 9:50
     */
    public PropertyDTO queryCachePropertyByPropKey(String propKey) {
        if (StringUtils.isEmpty(propKey)) {
            return null;
        }
        Object object = redisUtils.getRedisByKey(RedisConstants.PROPERTY, propKey);
        if (object != null) {
            return (PropertyDTO) object;
        }
        return null;
    }

    /**
     * 根据code名称从缓存中获取code信息
     *
     * @param: codeName
     * @author:zc
     * @date 2019/12/13 10:24
     */
    public List<CodeDTO> queryCacheCodeByCodeName(String codeName) {
        if (StringUtils.isEmpty(codeName)) {
            return null;
        }
        Object object = redisUtils.getRedisByKey(RedisConstants.CODE, codeName);
        if (object != null) {
            return (List<CodeDTO>) object;
        }
        return null;

    }

}

