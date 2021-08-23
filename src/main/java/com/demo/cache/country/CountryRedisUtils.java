package com.demo.cache.country;

/**
 * @author kj
 * @date 2021/8/19 11:01
 */

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import com.demo.aop.CommonRedisLogPrint;
import com.demo.base.countryManager.cache.CountryCacheDTO;
import com.demo.base.countryManager.cache.FindCountryCacheParam;
import com.demo.base.countryManager.dto.CountryDTO;
import com.demo.contants.RedisConstants;
import com.demo.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component


public class CountryRedisUtils {
    @Autowired
    private CountryRedisDao countryRedisDao;
    @Autowired
    private RedisUtils redisUtils;


    /*
     *
     * @author kj
     * @date 2021/8/20 9:56
     * @param []
     * @return void
     */
    @CommonRedisLogPrint(logRemark = "初始化country参数")
    public void initCountry() {
        redisUtils.del(RedisConstants.COUNTRY);
        List<CountryCacheDTO> countryCacheDTOList = findDbCountryList(null);
        redisUtils.putAllToRedis(RedisConstants.COUNTRY, countryCacheDTOList, "countryId");
    }

    /*
     *
     * @author kj
     * @date 2021/8/20 9:10
     * @param []
     * @return java.util.List<com.demo.base.countryManager.dto.CountryDTO>
     */
    private List<CountryCacheDTO> findDbCountryList(FindCountryCacheParam findCountryCacheParam) {
        List<CountryDTO> countryDTOList = countryRedisDao.findCountryList(findCountryCacheParam);
        if (CollectionUtil.isNotEmpty(countryDTOList)) {
            return countryDTOList.stream().map(countryDTO -> {
                CountryCacheDTO countryCacheDTO = CountryCacheDTO.builder().build();
                BeanUtil.copyProperties(countryDTO, countryCacheDTO, CopyOptions.create().ignoreNullValue());
                return countryCacheDTO;
            }).collect(Collectors.toList());
        }
        return null;
    }

    /*
     *
     * @author kj
     * @date 2021/8/20 9:10
     * @param [countryName]
     * @return void
     */
    public void updateCountry(Long countryId) {
        if (countryId == null) {
            return;
        }
        FindCountryCacheParam findCountryCacheParam = FindCountryCacheParam.builder().countryId(countryId).build();
        List<CountryCacheDTO> countryDTOList = findDbCountryList(findCountryCacheParam);
        if (countryDTOList != null && !countryDTOList.isEmpty()) {
            redisUtils.updateRedis(RedisConstants.COUNTRY, countryId.toString(), countryDTOList);
        }
    }

    /*
     * 
     * @author kj
     * @date 2021/8/20 15:05  
     * @param [countryName]
     * @return void
     */
    public void deleteCountry(String countryId) {
        redisUtils.deleteRedis(RedisConstants.COUNTRY, countryId);
    }
}
