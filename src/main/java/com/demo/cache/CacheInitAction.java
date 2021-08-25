package com.demo.cache;

import com.demo.action.BaseAction;
import com.demo.action.result.BaseResult;
import com.demo.action.result.ResultCode;
import com.demo.aop.CommonBusiness;
import com.demo.cache.city.CityRedisUtils;
import com.demo.cache.country.CountryRedisUtils;
import com.demo.cache.district.DistrictRedisUtils;
import com.demo.cache.province.ProvinceRedisUtils;
import com.demo.contants.RedisConstants;
import com.demo.system.redisCacheManager.request.InitRedisCacheByRedisConstantsParam;
import com.demo.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 初始化action
 * @author:yzc
 * @date 2020/3/27 16:41
 **/
@RestController
@RequestMapping("cacheInitAction")
@Slf4j
public class CacheInitAction extends BaseAction {

    @Autowired
    private CountryRedisUtils countryRedisUtils;
    @Autowired
    private ProvinceRedisUtils provinceRedisUtils;
    @Autowired
    private CityRedisUtils cityRedisUtils;
    @Autowired
    private DistrictRedisUtils districtRedisUtils;


    /**
     * 初始化缓存
     * 在每一个初始化方法加上自己redisKey和All的判断以便进行对某一个缓存初始化
     * @author:yzc
     * @date 2020/3/27 17:01
     * @return
     */
    @RequestMapping("initRedis")
    @CommonBusiness(logRemark = "初始化缓存")
    public BaseResult initRedis(@RequestBody(required = false) InitRedisCacheByRedisConstantsParam redisConstantsParam) {
        try {
            if (redisConstantsParam == null || StringUtils.isEmpty(redisConstantsParam.getRedisConstants())) {
                return returnFail(ResultCode.AUTH_PARAM_ERROR, "redis缓存标志不能为空");
            }
            String redisConstants = redisConstantsParam.getRedisConstants();
            //初始化country参数
            if (RedisConstants.ALL.equals(redisConstants) || RedisConstants.COUNTRY.equals(redisConstants)) {
                countryRedisUtils.initCountry();
            }
            if (RedisConstants.ALL.equals(redisConstants) || RedisConstants.PROVINCE.equals(redisConstants)) {
                provinceRedisUtils.initProvince();
            }
            if (RedisConstants.ALL.equals(redisConstants) || RedisConstants.CITY.equals(redisConstants)) {
                cityRedisUtils.initCity();
            }
            if (RedisConstants.ALL.equals(redisConstants) || RedisConstants.DISTRICT.equals(redisConstants)) {
                districtRedisUtils.initDistrict();
            }
            return returnSuccess("初始化缓存成功！");
        } catch (Exception e) {
            log.error("初始化缓存：初始化缓存失败", e);
        }
        return returnFail(ResultCode.ERROR, "初始化缓存失败");
    }
}
