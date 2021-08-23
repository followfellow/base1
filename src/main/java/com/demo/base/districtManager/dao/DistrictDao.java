package com.demo.base.districtManager.dao;

import com.demo.action.vo.QueryPage;
import com.demo.base.districtManager.dto.DistrictDTO;
import com.demo.base.districtManager.request.FindDistrictParam;
import com.demo.dbutils.BaseDAO;

import java.util.List;

/**
 * @author kj
 * @date 2021/8/13 14:43
 */
public interface DistrictDao extends BaseDAO {
    List<DistrictDTO> findDistrictList(FindDistrictParam findDistrictParam, QueryPage queryPage);

    List<DistrictDTO> finddistrictByName(String districtName, Long districtId);
}
