package com.demo.base.provinceManager.service;

import com.demo.base.provinceManager.dto.ProvinceDTO;
import com.demo.base.provinceManager.po.ProvinceDO;
import com.demo.base.provinceManager.request.FindAreaTreeParam;
import com.demo.base.provinceManager.request.FindProvinceParam;
import com.demo.base.provinceManager.response.FindAreaResult;

import java.util.List;


/**
 * 省份service
 *
 * @author:kj
 * @date:2021-08-20 16:54
 */
public interface ProvinceService {


    /**
     * 添加省份
     *
     * @param provinceDO
     * @author kj
     * @date 2021-08-20 16:54
     */
    void addProvince(ProvinceDO provinceDO);

    /**
     * 通过id查询省份
     *
     * @param ProvinceId
     * @author kj
     * @date 2021-08-20 16:54
     */
    ProvinceDO queryProvinceById(Long ProvinceId);

    /**
     * 查询省份列表
     *
     * @param findProvinceParam
     * @author kj
     * @date 2021-08-20 16:54
     */
    List<ProvinceDTO> findProvinceList(FindProvinceParam findProvinceParam);

    /**
     * 更新省份
     *
     * @param provinceDO
     * @author kj
     * @date 2021-08-20 16:54
     */
    void updateProvince(ProvinceDO provinceDO);

    /**
     * 删除省份
     *
     * @param ProvinceId
     * @author kj
     * @date 2021-08-20 16:54
     */
    void deleteProvince(Long ProvinceId);

    /*
     *
     * @author kj
     * @date 2021/8/24 11:19
     * @param [provinceName, provinceId]
     * @return boolean
     */
    boolean checkNameIfExist(String provinceName, Long provinceId);

    List<FindAreaResult> findProvinceNode(FindAreaTreeParam findAreaTreeParam);

    List<FindAreaResult> findCityNode(FindAreaTreeParam findAreaTreeParam);

    List<FindAreaResult> findDistrictNode(FindAreaTreeParam findAreaTreeParam);

    List<ProvinceDTO> findProvinceSelect(FindProvinceParam findProvinceParam);
}
