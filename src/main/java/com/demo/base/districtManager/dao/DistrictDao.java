package com.demo.base.districtManager.dao;

import com.demo.base.districtManager.dto.DistrictDTO;
import com.demo.base.districtManager.request.FindDistrictParam;
import com.demo.dbutils.BaseDAO;

import java.util.List;

/**
 * @author kj
 * @date 2021/8/13 14:43
 */
public interface DistrictDao extends BaseDAO {
    /*
     * 
     * @author kj
     * @date 2021/8/26 13:35  
     * @param [findDistrictParam, queryPage]
     * @return java.util.List<com.demo.base.districtManager.dto.DistrictDTO>
     */
    List<DistrictDTO> findDistrictList(FindDistrictParam findDistrictParam);

    /*
     * 
     * @author kj
     * @date 2021/8/26 13:38
     * @param [districtName, districtId]
     * @return java.util.List<com.demo.base.districtManager.dto.DistrictDTO>
     */
    List<DistrictDTO> finddistrictByName(String districtName, Long districtId);
}
