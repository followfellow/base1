package com.demo.base.provinceManager.action;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import com.demo.action.BaseAction;
import com.demo.action.result.ResultCode;
import com.demo.aop.CommonBusiness;
import com.demo.base.cityManager.cache.CityCacheDTO;
import com.demo.base.cityManager.service.CityService;
import com.demo.base.districtManager.cache.DistrictCacheDTO;
import com.demo.base.districtManager.service.DistrictService;
import com.demo.base.provinceManager.cache.ProvinceCacheDTO;
import com.demo.base.provinceManager.dto.ProvinceDTO;
import com.demo.base.provinceManager.po.ProvinceDO;
import com.demo.base.provinceManager.request.*;
import com.demo.base.provinceManager.response.FindProvinceResult;
import com.demo.base.provinceManager.response.QueryProvinceResult;
import com.demo.base.provinceManager.service.ProvinceService;
import com.demo.cache.QueryCacheUtils;
import com.demo.cache.province.ProvinceRedisUtils;
import com.demo.utils.PinyinUtils;
import com.demo.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 省份action
 *
 * @author:kj
 * @date:2021-08-20 16:54:56
 */

@RestController
@RequestMapping("provinceAction")
public class ProvinceAction extends BaseAction {

    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private CityService cityService;
    @Autowired
    private DistrictService districtService;
    @Autowired
    private ProvinceRedisUtils provinceRedisUtils;
    @Autowired
    QueryCacheUtils queryCacheUtils;


    /**
     * 查询省份列表
     *
     * @param findProvinceParam
     * @author kj
     * @date 2021-08-20 16:54:56
     */
    @RequestMapping("findProvinceList")
    @CommonBusiness(logRemark = "查询省份列表")
    public Object findProvinceList(@RequestBody(required = false) FindProvinceParam findProvinceParam) {
        if (findProvinceParam == null) {
            findProvinceParam = FindProvinceParam.builder().build();
        }
        List<ProvinceDTO> provinceDTOList = provinceService.findProvinceList(findProvinceParam);
        List<FindProvinceResult> findProvinceResultList = processProvinceInfo(provinceDTOList);

        int size = findProvinceResultList.size();
        FindProvinceResult findProvinceResult = FindProvinceResult.builder()
                .findProvinceResultList(findProvinceResultList)
                .provinceSize(size)
                .build();

        return returnSuccess("查询省份列表成功!", findProvinceResult);
    }

    /**
     * 处预定规则信息
     *
     * @param provinceDTOList
     * @author kj
     * @date 2021-08-20 16:54:56
     */
    private List<FindProvinceResult> processProvinceInfo(List<ProvinceDTO> provinceDTOList) {
        return provinceDTOList.stream().map(provinceDTO -> {
            FindProvinceResult findProvinceResult = FindProvinceResult.builder().build();
            BeanUtil.copyProperties(provinceDTO, findProvinceResult, CopyOptions.create().ignoreNullValue());
            return findProvinceResult;
        }).collect(Collectors.toList());
    }


    /**
     * 通过id查询省份
     *
     * @param queryProvinceParam
     * @author kj
     * @date 2021-08-20 16:54
     */
    @RequestMapping("queryProvinceById")
    @CommonBusiness(logRemark = "根据id查询省份")
    public Object queryProvinceById(@RequestBody(required = false) QueryProvinceParam queryProvinceParam) {
        if (queryProvinceParam == null || queryProvinceParam.getProvinceId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择查询省份!");
        }
        ProvinceDO provinceDO = provinceService.queryProvinceById(queryProvinceParam.getProvinceId());
        if (provinceDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到省份信息!");
        }
        QueryProvinceResult queryProvinceResult = QueryProvinceResult.builder()
                .build();
        BeanUtil.copyProperties(provinceDO, queryProvinceResult, CopyOptions.create().ignoreNullValue());
        return returnSuccess("查询省份成功!", queryProvinceResult);
    }


    /**
     * 添加省份
     *
     * @param addProvinceParam
     * @author kj
     * @date 2021-08-20 16:54
     */
    @RequestMapping("addProvince")
    @CommonBusiness(logRemark = "添加省份")
    public Object addProvince(@RequestBody(required = false) AddProvinceParam addProvinceParam) {
        String checkResult = checkAddProvinceParam(addProvinceParam);
        if (!StringUtils.isEmpty(checkResult)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, checkResult);
        }
        ProvinceDO provinceDO = ProvinceDO.builder()
                //暂时用
                .provinceId(System.currentTimeMillis())
//                .provinceId(numberMachineUtils.getTableID(NumberMachineConstants.PROVINCE_TABLE_ID_SEQ))
//                .businessId(getCurrUserOrgId())
                .build();
        BeanUtil.copyProperties(addProvinceParam, provinceDO, CopyOptions.create().ignoreNullValue());
        try {
            provinceDO.setProvinceChar(PinyinUtils.toPinYinUppercase(addProvinceParam.getProvinceName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "全称转换拼音失败!");
        }
        provinceService.addProvince(provinceDO);
        provinceRedisUtils.updateProvince(provinceDO.getProvinceId());
        return returnSuccess("添加省份成功!");
    }

    /**
     * 添加预定规则参数校验
     *
     * @param addProvinceParam
     * @author kj
     * @date 2021-08-20 16:54
     */
    private String checkAddProvinceParam(AddProvinceParam addProvinceParam) {
        if (addProvinceParam == null) {
            return "请输入省份信息!";
        }
        if (StringUtils.isEmpty(addProvinceParam.getProvinceName())) {
            return "请输入省份名称!";
        }
        if (provinceService.checkNameIfExist(addProvinceParam.getProvinceName(), null)) {
            return "省份名称已存在!";
        }
        if (addProvinceParam.getCertificateNo() == null) {
            return "请输入省份身份证编号";
        }
        return null;
    }


    /**
     * 修改省份
     *
     * @param updateProvinceParam
     * @author kj
     * @date 2021-08-20 16:54
     */
    @RequestMapping("updateProvince")
    @CommonBusiness(logRemark = "修改省份")
    public Object updateProvince(@RequestBody(required = false) UpdateProvinceParam updateProvinceParam) {
        String checkResult = checkUpdateProvinceParam(updateProvinceParam);
        if (!StringUtils.isEmpty(checkResult)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, checkResult);
        }
        ProvinceDO provinceDO = provinceService.queryProvinceById(updateProvinceParam.getProvinceId());
        if (provinceDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到省份信息!");
        }
        BeanUtil.copyProperties(updateProvinceParam, provinceDO, CopyOptions.create().ignoreNullValue());
        try {
            provinceDO.setProvinceChar(PinyinUtils.toPinYinUppercase(updateProvinceParam.getProvinceName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "全称转换拼音失败!");
        }
        provinceService.updateProvince(provinceDO);
        provinceRedisUtils.updateProvince(provinceDO.getProvinceId());
        return returnSuccess("修改省份成功!");
    }

    /**
     * 修改预定规则参数校验
     *
     * @param updateProvinceParam
     * @author kj
     * @date 2021-08-20 16:54
     */
    private String checkUpdateProvinceParam(UpdateProvinceParam updateProvinceParam) {
        if (updateProvinceParam == null || updateProvinceParam.getProvinceId() == null) {
            return "请选择修改省份!";
        }
        if (StringUtils.isEmpty(updateProvinceParam.getProvinceName())) {
            return "请输入省份名称!";
        }
        if (provinceService.checkNameIfExist(updateProvinceParam.getProvinceName(), updateProvinceParam.getProvinceId())) {
            return "省份名称已存在!";
        }
        return null;
    }

    /**
     * 删除省份
     *
     * @param deleteProvinceParam
     * @author kj
     * @date 2021-08-20 16:54
     */
    @RequestMapping("deleteProvince")
    @CommonBusiness(logRemark = "删除省份")
    public Object deleteProvince(@RequestBody(required = false) DeleteProvinceParam deleteProvinceParam) {
        if (deleteProvinceParam == null || deleteProvinceParam.getProvinceId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择删除省份!");
        }
        ProvinceDO provinceDO = provinceService.queryProvinceById(deleteProvinceParam.getProvinceId());
        if (provinceDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到省份信息!");
        }
        provinceService.deleteProvince(deleteProvinceParam.getProvinceId());
        provinceRedisUtils.deleteProvince(deleteProvinceParam.getProvinceId().toString());
        return returnSuccess("删除省份成功!");
    }

    /*
     * 返回所有地区信息
     * @author kj
     * @date 2021/9/1 14:52
     * @param [findAreaTreeParam]
     * @return java.lang.Object
     */
    @RequestMapping("findProvinceCityDistrictListFormCache")
    @CommonBusiness(logRemark = "查询所有")
    public Object findProvinceCityDistrictListFormCache(@RequestBody(required = false) FindAreaTreeParam findAreaTreeParam) {

        List<ProvinceCacheDTO> provinceCacheDTOList = queryCacheUtils.findCacheProvinceList();
        List<CityCacheDTO> cityCacheDTOList = queryCacheUtils.findCacheCityList();
        List<DistrictCacheDTO> districtCacheDTOList = queryCacheUtils.findCacheDistrictList();

        Map<Long, List<DistrictCacheDTO>> listMap1 = districtCacheDTOList.stream()
                .sorted(Comparator.comparing(DistrictCacheDTO::getDistrictId))
                .collect(Collectors.groupingBy(DistrictCacheDTO::getCityId));

        for (CityCacheDTO cityCacheDTO : cityCacheDTOList) {
            cityCacheDTO.setDistricts(listMap1.get(cityCacheDTO.getCityId()));
            if (CollectionUtil.isNotEmpty(cityCacheDTO.getDistricts())) {
                cityCacheDTO.setDistrictSize(cityCacheDTO.getDistricts().size());
            }
        }

        Map<Long, List<CityCacheDTO>> collect1 = cityCacheDTOList.stream()
                .sorted(Comparator.comparing(CityCacheDTO::getCityId))
                .collect(Collectors.groupingBy(CityCacheDTO::getProvinceId));

        for (ProvinceCacheDTO provinceCacheDTO : provinceCacheDTOList) {
            provinceCacheDTO.setCities(collect1.get(provinceCacheDTO.getProvinceId()));
            if (CollectionUtil.isNotEmpty(provinceCacheDTO.getCities())) {
                provinceCacheDTO.setCitySize(provinceCacheDTO.getCities().size());
            }
        }

        return provinceCacheDTOList.stream()
                .sorted(Comparator.comparing(ProvinceCacheDTO::getProvinceId))
                .collect(Collectors.toList());
    }


    /*
     * 根据给定参数查询
     * @author kj
     * @date 2021/9/1 14:52
     * @param [findAreaTreeParam]
     * @return java.lang.Object
     */
    @RequestMapping("findArea")
    @CommonBusiness(logRemark = "根据条件查询")
    public Object findArea(@RequestBody(required = false) FindAreaTreeParam findAreaTreeParam) {
        if (findAreaTreeParam == null) {
            return returnSuccess("查询成功!", findProvinceCityDistrictListFormCache(null));
        }
        if (findAreaTreeParam.getDistrictId() != null) {
            findAreaTreeParam.setCityId(queryCacheUtils.findCityIdByDistrictId(findAreaTreeParam.getDistrictId()));
            findAreaTreeParam.setProvinceId(queryCacheUtils.findProvinceIdByCityId(findAreaTreeParam.getCityId()));

            List<DistrictCacheDTO> districtCacheDTOList1 = queryCacheUtils.findDistrictByCityId(findAreaTreeParam.getCityId());
            int districtSize = districtCacheDTOList1.size();
            List<CityCacheDTO> cityCacheDTOList1 = queryCacheUtils.findCitiesByProvinceId(findAreaTreeParam.getProvinceId());
            int citySize = cityCacheDTOList1.size();

            List<DistrictCacheDTO> districtCacheDTOList = districtCacheDTOList1
                    .stream()
                    .filter(districtCacheDTO -> findAreaTreeParam.getDistrictId().equals(districtCacheDTO.getDistrictId()))
                    .collect(Collectors.toList());

            List<CityCacheDTO> cityCacheDTOList = cityCacheDTOList1
                    .stream()
                    .filter(cityCacheDTO -> findAreaTreeParam.getCityId().equals(cityCacheDTO.getCityId()))
                    .collect(Collectors.toList());

            return returnSuccess("查询成功!", citySetDistrict(findAreaTreeParam, cityCacheDTOList, districtCacheDTOList, citySize, districtSize));

        } else if (findAreaTreeParam.getCityId() != null) {
            findAreaTreeParam.setProvinceId(queryCacheUtils.findProvinceIdByCityId(findAreaTreeParam.getCityId()));

            List<DistrictCacheDTO> districtCacheDTOList = queryCacheUtils.findDistrictByCityId(findAreaTreeParam.getCityId())
                    .stream().sorted(Comparator.comparing(DistrictCacheDTO::getDistrictId))
                    .collect(Collectors.toList());
            int districtSize = districtCacheDTOList.size();
            List<CityCacheDTO> cityCacheDTOList1 = queryCacheUtils.findCitiesByProvinceId(findAreaTreeParam.getProvinceId());
            int citySize = cityCacheDTOList1.size();

            List<CityCacheDTO> cityCacheDTOList = cityCacheDTOList1
                    .stream()
                    .filter(cityCacheDTO -> findAreaTreeParam.getCityId().equals(cityCacheDTO.getCityId()))
                    .collect(Collectors.toList());
            return returnSuccess("查询成功!", citySetDistrict(findAreaTreeParam, cityCacheDTOList, districtCacheDTOList, citySize, districtSize));

        } else if (findAreaTreeParam.getProvinceId() != null) {
            List<DistrictCacheDTO> districtCacheDTOList = queryCacheUtils.findCacheDistrictList();

            Map<Long, List<DistrictCacheDTO>> listMap1 = districtCacheDTOList.stream()
                    .sorted(Comparator.comparing(DistrictCacheDTO::getDistrictId))
                    .collect(Collectors.groupingBy(DistrictCacheDTO::getCityId));

            List<CityCacheDTO> cityCacheDTOList = queryCacheUtils.findCitiesByProvinceId(findAreaTreeParam.getProvinceId())
                    .stream().sorted(Comparator.comparing(CityCacheDTO::getCityId))
                    .collect(Collectors.toList());
            int citySize = cityCacheDTOList.size();

            for (CityCacheDTO cityCacheDTO : cityCacheDTOList) {
                cityCacheDTO.setDistricts(listMap1.get(cityCacheDTO.getCityId()));
                if (CollectionUtil.isNotEmpty(cityCacheDTO.getDistricts())) {
                    cityCacheDTO.setDistrictSize(cityCacheDTO.getDistricts().size());
                }
            }
            return returnSuccess("查询成功!", filterProvince(findAreaTreeParam, cityCacheDTOList, citySize));
        }
        return null;
    }

    /*
     * 省份列表预处理
     * @author kj
     * @date 2021/9/1 14:52
     * @param [findAreaTreeParam, cityCacheDTOList]
     * @return java.lang.Object
     */
    private Object filterProvince(@RequestBody(required = false) FindAreaTreeParam findAreaTreeParam,
                                  List<CityCacheDTO> cityCacheDTOList, int citySize) {
        List<ProvinceCacheDTO> provinceCacheDTOList = queryCacheUtils.findCacheProvinceList()
                .stream()
                .filter(provinceCacheDTO -> findAreaTreeParam.getProvinceId().equals(provinceCacheDTO.getProvinceId()))
                .collect(Collectors.toList());
        for (ProvinceCacheDTO provinceCacheDTO : provinceCacheDTOList) {
            provinceCacheDTO.setCities(cityCacheDTOList);
            if (CollectionUtil.isNotEmpty(provinceCacheDTO.getCities())) {
                provinceCacheDTO.setCitySize(citySize);
            }
        }
        return provinceCacheDTOList.stream()
                .sorted(Comparator.comparing(ProvinceCacheDTO::getProvinceId))
                .collect(Collectors.toList());
    }

    /*
     * 城市列表预处理
     * @author kj
     * @date 2021/9/1 14:52
     * @param [findAreaTreeParam, districtCacheDTOList, cityCacheDTOList]
     * @return java.lang.Object
     */
    private Object citySetDistrict(@RequestBody(required = false) FindAreaTreeParam findAreaTreeParam,
                                   List<CityCacheDTO> cityCacheDTOList,
                                   List<DistrictCacheDTO> districtCacheDTOList,
                                   int citySize, int districtSize) {
        for (CityCacheDTO cityCacheDTO : cityCacheDTOList) {
            cityCacheDTO.setDistricts(districtCacheDTOList);
            if (CollectionUtil.isNotEmpty(cityCacheDTO.getDistricts())) {
                cityCacheDTO.setDistrictSize(districtSize);
            }
        }
        return filterProvince(findAreaTreeParam, cityCacheDTOList, citySize);
    }

}