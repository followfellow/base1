package com.demo.cache;

import com.demo.base.cityManager.cache.CityCacheDTO;
import com.demo.base.cityManager.dto.CityDTO;
import com.demo.base.countryManager.cache.CountryCacheDTO;
import com.demo.base.countryManager.dto.CountryDTO;
import com.demo.base.districtManager.cache.DistrictCacheDTO;
import com.demo.base.districtManager.dto.DistrictDTO;
import com.demo.base.provinceManager.cache.ProvinceCacheDTO;
import com.demo.base.provinceManager.dto.ProvinceDTO;
import com.demo.contants.RedisConstants;
import com.demo.redis.RedisUtils;
import com.demo.system.codeManager.dto.CodeDTO;
import com.demo.system.propertyManager.dto.PropertyDTO;
import com.demo.utils.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 从缓存中获取基础信息
 *
 * @author KL
 */
@Component
@Slf4j
public class QueryCacheUtils {

    @Autowired
    private RedisUtils redisUtils;


    /*
     *
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
     *
     * @author kj
     * @date 2021/8/25 16:17
     * @param [countryId]
     * @return com.demo.base.countryManager.cache.CountryCacheDTO
     */
    public CountryCacheDTO findCountry(Long countryId) {
        Object object = redisUtils.getRedisByKey(RedisConstants.COUNTRY, countryId.toString());
        return (CountryCacheDTO) object;
    }

    /*
     *
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
     * 
     * @author kj
     * @date 2021/8/25 16:22  
     * @param [provinceId]
     * @return com.demo.base.provinceManager.cache.ProvinceCacheDTO
     */
    public ProvinceCacheDTO findProvince(Long provinceId) {
        Object object = redisUtils.getRedisByKey(RedisConstants.PROVINCE, provinceId.toString());
        return (ProvinceCacheDTO) object;
    }

    /*
     *
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
     *
     * @author kj
     * @date 2021/8/25 11:53
     * @param [cityId]
     * @return java.lang.String
     */
    public String findCacheCityListById(Long cityId) {
        if (cityId == null) {
            return null;
        }
        Object object = redisUtils.getRedisByKey(RedisConstants.CITY, cityId.toString());
        CityCacheDTO cityCacheDTO = (CityCacheDTO) object;
        return cityCacheDTO.getCityName();
    }

    /*
     *
     * @author kj
     * @date 2021/8/25 11:53
     * @param [cityId]
     * @return com.demo.base.cityManager.cache.CityCacheDTO
     */
    public CityCacheDTO findCity(Long cityId) {
        Object object = redisUtils.getRedisByKey(RedisConstants.CITY, cityId.toString());
        return (CityCacheDTO) object;
    }

    /*
     *
     * @author kj
     * @date 2021/8/25 16:03
     * @param [provinceId]
     * @return java.util.List<java.lang.String>
     */
    public List<String> findCityListByProvinceId(Long provinceId) {
        List<String> list = Lists.newArrayList();
        List<CityCacheDTO> cacheCityList = findCacheCityList();
        for (CityCacheDTO cityCacheDTO : cacheCityList) {
            if (cityCacheDTO.getProvinceId().equals(provinceId)) {
                list.add(cityCacheDTO.getCityName());
            }
        }
        return list;
    }

    /*
     *
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
     * 
     * @author kj
     * @date 2021/8/25 16:23  
     * @param [districtId]
     * @return com.demo.base.districtManager.cache.DistrictCacheDTO
     */
    public DistrictCacheDTO findDistrict(Long districtId) {
        Object object = redisUtils.getRedisByKey(RedisConstants.DISTRICT, districtId.toString());
        return (DistrictCacheDTO) object;
    }

    /*
     * 
     * @author kj
     * @date 2021/8/25 16:30  
     * @param [cityId]
     * @return java.util.List<java.lang.String>
     */
    public List<String> findDistrictListByCityId(Long cityId) {
        List<String> list = Lists.newArrayList();
        List<DistrictCacheDTO> cacheDistrictList = findCacheDistrictList();
        for (DistrictCacheDTO districtCacheDTO : cacheDistrictList) {
            if (districtCacheDTO.getCityId().equals(cityId)) {
                list.add(districtCacheDTO.getDistrictName());
            }
        }
        return list;
    }

}

