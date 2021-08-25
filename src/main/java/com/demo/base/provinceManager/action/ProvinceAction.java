package com.demo.base.provinceManager.action;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.demo.action.BaseAction;
import com.demo.action.result.ResultCode;
import com.demo.action.vo.QueryPage;
import com.demo.aop.CommonBusiness;
import com.demo.base.provinceManager.dto.ProvinceDTO;
import com.demo.base.provinceManager.po.ProvinceDO;
import com.demo.base.provinceManager.request.*;
import com.demo.base.provinceManager.response.FindProvinceResult;
import com.demo.base.provinceManager.response.QueryProvinceResult;
import com.demo.base.provinceManager.service.ProvinceService;
import com.demo.cache.district.DistrictRedisUtils;
import com.demo.cache.province.ProvinceRedisUtils;
import com.demo.utils.PinyinUtils;
import com.demo.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    private ProvinceRedisUtils provinceRedisUtils;

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
        QueryPage queryPage = initQueryPage(findProvinceParam);
        List<ProvinceDTO> provinceDTOList = provinceService.findProvinceList(findProvinceParam, queryPage);
        List<FindProvinceResult> findProvinceResultList = processProvinceInfo(provinceDTOList);
        return returnSuccessListByPage(findProvinceResultList, queryPage, "查询省份列表成功!");
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

}