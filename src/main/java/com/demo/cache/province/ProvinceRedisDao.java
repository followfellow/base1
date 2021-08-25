package com.demo.cache.province;

import com.demo.base.provinceManager.cache.FindProvinceCacheParam;
import com.demo.base.provinceManager.dto.ProvinceDTO;
import com.demo.dbutils.BaseDAO;

import java.util.List;

/**
 * @author kj
 * @date 2021/8/23 18:43
 */
public interface ProvinceRedisDao extends BaseDAO {
    List<ProvinceDTO> findProvinceList(FindProvinceCacheParam findProvinceCacheParam);
}
