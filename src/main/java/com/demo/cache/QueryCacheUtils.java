package com.demo.cache;

import com.demo.base.cityManager.dto.CityDTO;
import com.demo.base.countryManager.dto.CountryDTO;
import com.demo.base.districtManager.dto.DistrictDTO;
import com.demo.base.provinceManager.dto.ProvinceDTO;
import com.demo.contants.RedisConstants;
import com.demo.redis.RedisUtils;
import com.demo.system.codeManager.dto.CodeDTO;
import com.demo.system.propertyManager.dto.PropertyDTO;
import com.demo.utils.StringUtils;
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
    public List<CountryDTO> findCacheCountryList(){
        List<Object> objectList=redisUtils.findAllByKey(RedisConstants.COUNTRY);
        if (objectList!=null||!objectList.isEmpty()){
            return (List<CountryDTO>) (List) objectList;
        }
        return null;
    }
    /*
     *
     * @author kj
     * @date 2021/8/23 19:31
     * @param []
     * @return java.util.List<com.demo.base.provinceManager.dto.ProvinceDTO>
     */
    public List<ProvinceDTO> findCacheProvinceList(){
        List<Object> objectList=redisUtils.findAllByKey(RedisConstants.PROVINCE);
        if (objectList!=null||!objectList.isEmpty()){
            return (List<ProvinceDTO>) (List) objectList;
        }
        return null;
    }
    /*
     *
     * @author kj
     * @date 2021/8/23 19:32
     * @param []
     * @return java.util.List<com.demo.base.cityManager.dto.CityDTO>
     */
    public List<CityDTO> findCacheCityList(){
        List<Object> objectList=redisUtils.findAllByKey(RedisConstants.CITY);
        if (objectList!=null||!objectList.isEmpty()){
            return (List<CityDTO>) (List) objectList;
        }
        return null;
    }
    /*
     *
     * @author kj
     * @date 2021/8/23 19:32
     * @param []
     * @return java.util.List<com.demo.base.districtManager.dto.DistrictDTO>
     */
    public List<DistrictDTO> findCacheDistrictList(){
        List<Object> objectList=redisUtils.findAllByKey(RedisConstants.DISTRICT);
        if (objectList!=null||!objectList.isEmpty()){
            return (List<DistrictDTO>) (List) objectList;
        }
        return null;
    }



    /**
     * 从缓存中获取系统参数集合
     *
     * @author:zc
     * @date 2019/12/13 9:57
     */
    public List<PropertyDTO> findCachePropertyList() {
        List<Object> objectList = redisUtils.findAllByKey(RedisConstants.PROPERTY);
        if (objectList != null || !objectList.isEmpty()) {
            return (List<PropertyDTO>) (List) objectList;
        }
        return null;
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
     * 根据参数键从缓存中获取参数值
     *
     * @param: propKey
     * @author:zc
     * @date 2019/12/13 9:55
     */
    public String queryCachePropertyValueByPropKey(String propKey) {
        if (StringUtils.isEmpty(propKey)) {
            return null;
        }
        PropertyDTO property = queryCachePropertyByPropKey(propKey);
        if (property != null) {
            return property.getPropValue();
        }
        return null;
    }

    /**
     * 从缓存中获取所有Code信息集合
     *
     * @author:zc
     * @date 2019/12/13 10:19
     */
    public List<List<CodeDTO>> findCacheCodeList() {
        List<Object> objectList = redisUtils.findAllByKey(RedisConstants.CODE);
        if (objectList != null && !objectList.isEmpty()) {
            return (List<List<CodeDTO>>) (List) objectList;
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
            return (List<CodeDTO>) (List) object;
        }
        return null;
    }

    /**
     * 根据codekey从缓存中获取code值
     *
     * @param: codeKey
     * @author:zc
     * @date 2019/12/13 10:24
     */
    public String queryCacheCodeValueByCodeKey(String codeName, String codeKey) {
        if (StringUtils.isEmpty(codeName) || StringUtils.isEmpty(codeKey)) {
            return null;
        }
        Object object = redisUtils.getRedisByKey(RedisConstants.CODE, codeName);
        if (object != null) {
            List<CodeDTO> list = (List) object;
            for (CodeDTO codeDTO : list) {
                if (codeKey.equals(codeDTO.getCodeKey())) {
                    return codeDTO.getCodeValue();
                }
            }
        }
        return null;
    }

    /**
     * 根据codekey从缓存中获取code信息
     *
     * @param: codeKey
     * @author:zc
     * @date 2019/12/13 10:24
     */
    public CodeDTO queryCacheCodeByCodeKey(String codeName, String codeKey) {
        if (StringUtils.isEmpty(codeKey) || StringUtils.isEmpty(codeKey)) {
            return null;
        }
        Object object = redisUtils.getRedisByKey(RedisConstants.CODE, codeName);
        if (object != null) {
            List<CodeDTO> list = (List) object;
            for (CodeDTO codeDTO : list) {
                if (codeKey.equals(codeDTO.getCodeKey())) {
                    return codeDTO;
                }
            }
        }
        return null;
    }

}

