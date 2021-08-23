package com.demo.cache.city;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import com.demo.aop.CommonRedisLogPrint;
import com.demo.base.cityManager.cache.CityCacheDTO;
import com.demo.base.cityManager.cache.CityCacheDTO;
import com.demo.base.cityManager.cache.FindCityCacheParam;
import com.demo.base.cityManager.dto.CityDTO;
import com.demo.cache.city.CityRedisDao;
import com.demo.contants.RedisConstants;
import com.demo.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kj
 * @date 2021/8/23 18:45
 */
@Component

public class CityRedisUtils {
    @Autowired
    private CityRedisDao cityRedisDao;
    @Autowired
    private RedisUtils redisUtils;

    @CommonRedisLogPrint(logRemark = "初始化city参数")
    public void initCity() {
        redisUtils.del(RedisConstants.CITY);
        List<CityCacheDTO> cityCacheDTOList = findDbCityList(null);
        redisUtils.putAllToRedis(RedisConstants.CITY, cityCacheDTOList, "cityId");
    }
    private List<CityCacheDTO> findDbCityList(FindCityCacheParam findCityCacheParam) {
        List<CityDTO> cityDTOList = cityRedisDao.findCityList(findCityCacheParam);
        if (CollectionUtil.isNotEmpty(cityDTOList)) {
            return cityDTOList.stream().map(cityDTO -> {
                CityCacheDTO cityCacheDTO = CityCacheDTO.builder().build();
                BeanUtil.copyProperties(cityDTO, cityCacheDTO, CopyOptions.create().ignoreNullValue());
                return cityCacheDTO;
            }).collect(Collectors.toList());
        }
        return null;
    }

    /*
     *
     * @author kj
     * @date 2021/8/20 9:10
     * @param [cityName]
     * @return void
     */
    public void updateCity(Long cityId) {
        if (cityId == null) {
            return;
        }
        FindCityCacheParam findCityCacheParam = FindCityCacheParam.builder().cityId(cityId).build();
        List<CityCacheDTO> cityDTOList = findDbCityList(findCityCacheParam);
        if (cityDTOList != null && !cityDTOList.isEmpty()) {
            redisUtils.updateRedis(RedisConstants.CITY, cityId.toString(), cityDTOList);
        }
    }

    /*
     *
     * @author kj
     * @date 2021/8/20 15:05
     * @param [cityName]
     * @return void
     */
    public void deleteCity(String cityId) {
        redisUtils.deleteRedis(RedisConstants.CITY, cityId);
    }
}
