package com.demo.cache.province;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import com.demo.aop.CommonRedisLogPrint;
import com.demo.base.provinceManager.cache.ProvinceCacheDTO;
import com.demo.base.provinceManager.cache.FindProvinceCacheParam;
import com.demo.base.provinceManager.dto.ProvinceDTO;
import com.demo.base.provinceManager.cache.ProvinceCacheDTO;
import com.demo.cache.province.ProvinceRedisDao;
import com.demo.contants.RedisConstants;
import com.demo.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kj
 * @date 2021/8/23 18:44
 */
@Component

public class ProvinceRedisUtils {
    @Autowired
    private ProvinceRedisDao provinceRedisDao;
    @Autowired
    private RedisUtils redisUtils;

    @CommonRedisLogPrint(logRemark = "初始化province参数")
    public void initProvince() {
        redisUtils.del(RedisConstants.PROVINCE);
        List<ProvinceCacheDTO> provinceCacheDTOList = findDbProvinceList(null);
        redisUtils.putAllToRedis(RedisConstants.PROVINCE, provinceCacheDTOList, "provinceId");
    }
    private List<ProvinceCacheDTO> findDbProvinceList(FindProvinceCacheParam findProvinceCacheParam) {
        List<ProvinceDTO> provinceDTOList = provinceRedisDao.findProvinceList(findProvinceCacheParam);
        if (CollectionUtil.isNotEmpty(provinceDTOList)) {
            return provinceDTOList.stream().map(provinceDTO -> {
                ProvinceCacheDTO provinceCacheDTO = ProvinceCacheDTO.builder().build();
                BeanUtil.copyProperties(provinceDTO, provinceCacheDTO, CopyOptions.create().ignoreNullValue());
                return provinceCacheDTO;
            }).collect(Collectors.toList());
        }
        return null;
    }

    /*
     *
     * @author kj
     * @date 2021/8/20 9:10
     * @param [provinceName]
     * @return void
     */
    public void updateProvince(Long provinceId) {
        if (provinceId == null) {
            return;
        }
        FindProvinceCacheParam findProvinceCacheParam = FindProvinceCacheParam.builder().provinceId(provinceId).build();
        List<ProvinceCacheDTO> provinceDTOList = findDbProvinceList(findProvinceCacheParam);
        if (provinceDTOList != null && !provinceDTOList.isEmpty()) {
            redisUtils.updateRedis(RedisConstants.PROVINCE, provinceId.toString(), provinceDTOList);
        }
    }

    /*
     *
     * @author kj
     * @date 2021/8/20 15:05
     * @param [provinceName]
     * @return void
     */
    public void deleteProvince(String provinceId) {
        redisUtils.deleteRedis(RedisConstants.PROVINCE, provinceId);
    }
}
