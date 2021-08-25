package com.demo.base.provinceManager.dao;

import com.demo.action.vo.QueryPage;
import com.demo.base.provinceManager.dto.ProvinceDTO;
import com.demo.base.provinceManager.request.FindProvinceParam;
import com.demo.dbutils.BaseDAO;

import java.util.List;

/**
 * 省份dao
 *
 * @author:kj
 * @date:2021-08-20 16:54
 */
public interface ProvinceDao extends BaseDAO {
    /**
     * 查询省份列表
     *
     * @param findProvinceParam
     * @param queryPage
     * @author kj
     * @date 2021-08-20 16:54
     */
    List<ProvinceDTO> findProvinceList(FindProvinceParam findProvinceParam, QueryPage queryPage);

    /*
     *
     * @author kj
     * @date 2021/8/24 11:34
     * @param [provinceName, provinceId]
     * @return java.util.List<com.demo.base.provinceManager.dto.ProvinceDTO>
     */
    List<ProvinceDTO> findProvinceByName(String provinceName, Long provinceId);
}
