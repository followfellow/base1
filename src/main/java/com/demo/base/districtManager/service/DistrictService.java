package com.demo.base.districtManager.service;

import com.demo.action.vo.QueryPage;
import com.demo.base.districtManager.dto.DistrictDTO;
import com.demo.base.districtManager.po.DistrictDO;
import com.demo.base.districtManager.request.FindDistrictParam;

import java.util.List;

/**
 * @author kj
 * @date 2021/8/13 14:44
 */
public interface DistrictService {
    List<DistrictDTO> findDistrictList(FindDistrictParam findDistrictParam, QueryPage queryPage);

    void addDistrict(DistrictDO districtDO);

    DistrictDO queryDistrictById(Long districtId);

    void updateDistrict(DistrictDO districtDO);

    void deleteDistrict(Long districtId);

    boolean checkNameIfExist(String districtName, Long districtId);
}
