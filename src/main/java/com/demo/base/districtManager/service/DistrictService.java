package com.demo.base.districtManager.service;

import com.demo.base.districtManager.dto.DistrictDTO;
import com.demo.base.districtManager.po.DistrictDO;
import com.demo.base.districtManager.request.FindDistrictParam;

import java.util.List;

/**
 * @author kj
 * @date 2021/8/13 14:44
 */
public interface DistrictService {
    /*
     * 
     * @author kj
     * @date 2021/8/26 13:34  
     * @param [findDistrictParam, queryPage]
     * @return java.util.List<com.demo.base.districtManager.dto.DistrictDTO>
     */
    List<DistrictDTO> findDistrictList(FindDistrictParam findDistrictParam);

    /*
     * 
     * @author kj
     * @date 2021/8/26 13:34  
     * @param [districtDO]
     * @return void
     */
    void addDistrict(DistrictDO districtDO);

    /*
     * 
     * @author kj
     * @date 2021/8/26 13:34  
     * @param [districtId]
     * @return com.demo.base.districtManager.po.DistrictDO
     */
    DistrictDO queryDistrictById(Long districtId);

    /*
     * 
     * @author kj
     * @date 2021/8/26 13:34  
     * @param [districtDO]
     * @return void
     */
    void updateDistrict(DistrictDO districtDO);

    /*
     * 
     * @author kj
     * @date 2021/8/26 13:34  
     * @param [districtId]
     * @return void
     */
    void deleteDistrict(Long districtId);

    /*
     * 
     * @author kj
     * @date 2021/8/26 13:38
     * @param [districtName, districtId]
     * @return boolean
     */
    boolean checkNameIfExist(String districtName, Long districtId);

    List<DistrictDTO> findDistrictSelect(FindDistrictParam findDistrictParam);
}
