package com.demo.base.provinceManager.action;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import com.demo.action.BaseAction;
import com.demo.action.result.ResultCode;
import com.demo.aop.CommonBusiness;
import com.demo.base.provinceManager.dto.ProvinceDTO;
import com.demo.base.provinceManager.po.ProvinceDO;
import com.demo.base.provinceManager.request.*;
import com.demo.base.provinceManager.response.FindAreaResult;
import com.demo.base.provinceManager.response.FindProvinceResult;
import com.demo.base.provinceManager.response.QueryProvinceResult;
import com.demo.base.provinceManager.service.ProvinceService;
import com.demo.cache.QueryCacheUtils;
import com.demo.cache.province.ProvinceRedisUtils;
import com.demo.contants.Constants;
import com.demo.utils.PinyinUtils;
import com.demo.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * 省份action
 *
 * @author:kj
 * @date:2021-08-20 16:54:56
 */

@RestController
@RequestMapping(Constants.OAPI+"provinceAction")
//@RequestMapping("provinceAction")
public class ProvinceAction extends BaseAction {

    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private ProvinceRedisUtils provinceRedisUtils;
    @Autowired
    QueryCacheUtils queryCacheUtils;


    /**
     * 处预定规则信息
     *
     * @param provinceDTOList
     * @author kj
     * @date 2021-08-20 16:54:56
     */
    private List<FindProvinceResult> processProvinceInfo(List<ProvinceDTO> provinceDTOList) {
        return provinceDTOList.stream()
//                .sorted(Comparator.comparing(ProvinceDTO::getProvinceId))
                .map(provinceDTO -> {
                    FindProvinceResult findProvinceResult = FindProvinceResult.builder().build();
                    BeanUtil.copyProperties(provinceDTO, findProvinceResult, CopyOptions.create().ignoreNullValue());
                    return findProvinceResult;
                }).collect(Collectors.toList());
    }

    /*
     *
     * @author kj
     * @date 2021/9/6 14:39
     * @param [findProvinceParam]
     * @return java.lang.Object
     */
    @RequestMapping("findProvinceSelect")
    @CommonBusiness(logRemark = "查询省份下拉")
    @PreAuthorize("hasAuthority('provinceAction:findProvinceSelect')")
    public Object findProvinceSelect(@RequestBody(required = false) FindProvinceParam findProvinceParam) {
        if (findProvinceParam == null) {
            findProvinceParam = FindProvinceParam.builder().build();
        }
        List<ProvinceDTO> provinceDTOList = provinceService.findProvinceSelect(findProvinceParam);
        List<FindProvinceResult> findProvinceResultList = processProvinceInfo(provinceDTOList);
        return returnSuccess("查询省份下拉成功!", findProvinceResultList);
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
    @PreAuthorize("hasAuthority('provinceAction:queryProvinceById')")
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
    @PreAuthorize("hasAuthority('provinceAction:addProvince')")
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
    @PreAuthorize("hasAuthority('provinceAction:updateProvince')")
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
    @PreAuthorize("hasAuthority('provinceAction:deleteProvince')")
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
     *
     * @author kj
     * @date 2021/9/6 17:08
     * @param [findAreaTreeParam]
     * @return java.lang.Object
     */
    @RequestMapping("ListByCondition")
    @CommonBusiness(logRemark = "根据条件查询")
    @PreAuthorize("hasAuthority('provinceAction:ListByCondition')")
    public Object ListByCondition(@RequestBody(required = false) FindAreaTreeParam findAreaTreeParam) {

        List<FindAreaResult> provinceList = provinceService.findProvinceNode(null);
        List<FindAreaResult> cityList = provinceService.findCityNode(null);
        List<FindAreaResult> districtList = provinceService.findDistrictNode(null);

        //1.无参
        if ((findAreaTreeParam == null) || ((findAreaTreeParam.getProvinceId() == null) && (findAreaTreeParam.getCityId() == null) && (findAreaTreeParam.getDistrictId() == null))) {
            lowerListSize2UpperList(provinceList, cityList, null);
            return returnSuccess("查询省市县成功！", provinceList);
        }
        //2.有district
        if (findAreaTreeParam.getDistrictId() != null) {
            //cityId
            LinkedHashMap<Long, List<FindAreaResult>> districtMapByDistrictId = districtList.stream().collect(Collectors.groupingBy(FindAreaResult::getId, LinkedHashMap::new, Collectors.toList()));
            Long cityId = districtMapByDistrictId.get(findAreaTreeParam.getDistrictId()).get(0).getPid();
            findAreaTreeParam.setCityId(cityId);
            //provinceId
            LinkedHashMap<Long, List<FindAreaResult>> cityMapByCityId = cityList.stream().collect(Collectors.groupingBy(FindAreaResult::getId, LinkedHashMap::new, Collectors.toList()));
            Long provinceId = cityMapByCityId.get(findAreaTreeParam.getCityId()).get(0).getPid();
            findAreaTreeParam.setProvinceId(provinceId);
            
            //districtSortedWithSize0
            List<FindAreaResult> sortedDistrictList = sortedDistrictList(findAreaTreeParam, districtList);

            //districtSize2CityListWith0
            lowerListSize2UpperList(cityList, sortedDistrictList, cityId);
            //citySortedWithSize0
            List<FindAreaResult> sortedCityList = sortedCityList(findAreaTreeParam, cityList);

            //citySize2ProvinceListWith0
            lowerListSize2UpperList(provinceList, sortedCityList, provinceId);
            //provinceSorted
            return sortedProvinceList(findAreaTreeParam, provinceList);
        }
        //3.有city
        if (findAreaTreeParam.getCityId() != null) {
            //provinceId
            LinkedHashMap<Long, List<FindAreaResult>> cityMapByCityId = cityList.stream().collect(Collectors.groupingBy(FindAreaResult::getId, LinkedHashMap::new, Collectors.toList()));
            Long provinceId = cityMapByCityId.get(findAreaTreeParam.getCityId()).get(0).getPid();
            findAreaTreeParam.setProvinceId(provinceId);

            //districtSize2CityListWith0
            lowerListSize2UpperList(cityList, districtList, null);
            //citySortedWithSize0
            List<FindAreaResult> sortedCityList = sortedCityList(findAreaTreeParam, cityList);

            //citySize2ProvinceListWith0
            lowerListSize2UpperList(provinceList, sortedCityList, provinceId);
            //provinceSorted
            return sortedProvinceList(findAreaTreeParam, provinceList);
        }
        //4.有province
        if (findAreaTreeParam.getProvinceId() != null) {
            //citySize2ProvinceList
            lowerListSize2UpperList(provinceList, cityList, null);
            //provinceSorted
            return sortedProvinceList(findAreaTreeParam, provinceList);
        }
        return null;
    }

    /*
     * 
     * @author kj
     * @date 2021/9/7 9:09  
     * @param [findAreaTreeParam, provinceList]
     * @return java.lang.Object
     */
    private Object sortedProvinceList(FindAreaTreeParam findAreaTreeParam, List<FindAreaResult> provinceList) {
        int provinceIndex = IntStream.range(0, provinceList.size())
                .filter(i -> provinceList.get(i).getId().equals(findAreaTreeParam.getProvinceId()))
                .findFirst().orElse(-1);
        provinceList.add(0, provinceList.remove(provinceIndex));
        return returnSuccess("查询省市县成功！", provinceList);
    }

    /*
     * 
     * @author kj
     * @date 2021/9/7 9:09  
     * @param [findAreaTreeParam, cityList]
     * @return java.util.List<com.demo.base.provinceManager.response.FindAreaResult>
     */
    private List<FindAreaResult> sortedCityList(FindAreaTreeParam findAreaTreeParam, List<FindAreaResult> cityList) {
        int cityIndex = IntStream.range(0, cityList.size())
                .filter(i -> cityList.get(i).getId().equals(findAreaTreeParam.getCityId()))
                .findFirst().orElse(-1);
        cityList.add(0, cityList.remove(cityIndex));
        return cityList;
    }

    /*
     * 
     * @author kj
     * @date 2021/9/7 9:09  
     * @param [findAreaTreeParam, districtList]
     * @return java.util.List<com.demo.base.provinceManager.response.FindAreaResult>
     */
    private List<FindAreaResult> sortedDistrictList(FindAreaTreeParam findAreaTreeParam, List<FindAreaResult> districtList){
        int districtIndex = IntStream.range(0, districtList.size())
                .filter(i -> districtList.get(i).getId().equals(findAreaTreeParam.getDistrictId()))
                .findFirst().orElse(-1);
        districtList.add(0, districtList.remove(districtIndex));
        return districtList;
    }

    /*
     * 计算下层列表大小存入上层列表，若有上层id传入，则将第一组数据传入上层
     * @author kj
     * @date 2021/9/7 8:55
     * @param [upperList, lowerList, upperId]
     * @return void
     */
    private void lowerListSize2UpperList(List<FindAreaResult> upperList, List<FindAreaResult> lowerList, Long upperId) {
        LinkedHashMap<Long, List<FindAreaResult>> lowerMap = lowerList.stream().collect(Collectors.groupingBy(FindAreaResult::getPid, LinkedHashMap::new, Collectors.toList()));
        for (FindAreaResult findAreaResult : upperList) {
            if (upperId != null) {
                if (upperId.equals(findAreaResult.getId())) {
                    findAreaResult.setChildren(lowerMap.get(findAreaResult.getId()));
                }
            }
            if (CollectionUtil.isNotEmpty(lowerMap.get(findAreaResult.getId()))) {
                findAreaResult.setSize(lowerMap.get(findAreaResult.getId()).size());
            }
        }
    }

}