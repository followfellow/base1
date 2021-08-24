package com.demo.cache.district;

import com.demo.base.districtManager.cache.FindDistrictCacheParam;
import com.demo.base.districtManager.dto.DistrictDTO;
import com.demo.dbutils.BaseDAO;

import java.util.List;

/**
 * @author kj
 * @date 2021/8/23 18:50
 */
public interface DistrictRedisDao extends BaseDAO {
    List<DistrictDTO> findDistrictList(FindDistrictCacheParam findDistrictCacheParam);
}
