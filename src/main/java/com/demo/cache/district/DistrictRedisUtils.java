package com.demo.cache.district;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import com.demo.aop.CommonRedisLogPrint;
import com.demo.base.districtManager.cache.DistrictCacheDTO;
import com.demo.base.districtManager.cache.FindDistrictCacheParam;
import com.demo.base.districtManager.dto.DistrictDTO;
import com.demo.base.districtManager.cache.DistrictCacheDTO;
import com.demo.cache.district.DistrictRedisDao;
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

public class DistrictRedisUtils {
    @Autowired
    private DistrictRedisDao districtRedisDao;
    @Autowired
    private RedisUtils redisUtils;


    @CommonRedisLogPrint(logRemark = "初始化district参数")
    public void initDistrict() {
        redisUtils.del(RedisConstants.DISTRICT);
        List<DistrictCacheDTO> districtCacheDTOList = findDbDistrictList(null);
        redisUtils.putAllToRedis(RedisConstants.DISTRICT, districtCacheDTOList, "districtId");
    }
    private List<DistrictCacheDTO> findDbDistrictList(FindDistrictCacheParam findDistrictCacheParam) {
        List<DistrictDTO> districtDTOList = districtRedisDao.findDistrictList(findDistrictCacheParam);
        if (CollectionUtil.isNotEmpty(districtDTOList)) {
            return districtDTOList.stream().map(districtDTO -> {
                DistrictCacheDTO districtCacheDTO = DistrictCacheDTO.builder().build();
                BeanUtil.copyProperties(districtDTO, districtCacheDTO, CopyOptions.create().ignoreNullValue());
                return districtCacheDTO;
            }).collect(Collectors.toList());
        }
        return null;
    }

    /*
     *
     * @author kj
     * @date 2021/8/20 9:10
     * @param [districtName]
     * @return void
     */
    public void updateDistrict(Long districtId) {
        if (districtId == null) {
            return;
        }
        FindDistrictCacheParam findDistrictCacheParam = FindDistrictCacheParam.builder().districtId(districtId).build();
        List<DistrictCacheDTO> districtDTOList = findDbDistrictList(findDistrictCacheParam);
        if (districtDTOList != null && !districtDTOList.isEmpty()) {
            redisUtils.updateRedis(RedisConstants.DISTRICT, districtId.toString(), districtDTOList);
        }
    }

    /*
     *
     * @author kj
     * @date 2021/8/20 15:05
     * @param [districtName]
     * @return void
     */
    public void deleteDistrict(String districtId) {
        redisUtils.deleteRedis(RedisConstants.DISTRICT, districtId);
    }
}
