package com.demo.base.districtManager.action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.demo.action.BaseAction;
import com.demo.action.result.ResultCode;
import com.demo.aop.CommonBusiness;
import com.demo.base.districtManager.dto.DistrictDTO;
import com.demo.base.districtManager.po.DistrictDO;
import com.demo.base.districtManager.request.*;
import com.demo.base.districtManager.response.FindDistrictResult;
import com.demo.base.districtManager.response.QueryDistrictResult;
import com.demo.base.districtManager.service.DistrictService;
import com.demo.base.provinceManager.request.FindAreaTreeParam;
import com.demo.base.provinceManager.response.FindAreaResult;
import com.demo.base.provinceManager.service.ProvinceService;
import com.demo.cache.district.DistrictRedisUtils;
import com.demo.contants.Constants;
import com.demo.contants.NumberMachineConstants;
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

/**
 * @author kj
 * @date 2021/8/13 14:43
 */
@RestController
@RequestMapping(Constants.OAPI+"districtAction")
//@RequestMapping("districtAction")
public class DistrictAction extends BaseAction {

    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private DistrictService districtService;
    @Autowired
    private DistrictRedisUtils districtRedisUtils;

    /*
     * 查询地区列表
     * @author kj
     * @date 2021/8/12 18:52
     * @param [findDistrictParam, request]
     * @return java.lang.Object
     */
    @RequestMapping("findDistrictList")
    @CommonBusiness(logRemark = "查询地区成功")
    @PreAuthorize("hasAuthority('districtAction:findDistrictList')")
    public Object findDistrictList(@RequestBody(required = false) FindAreaTreeParam findAreaTreeParam) {
        if (findAreaTreeParam == null) {
            findAreaTreeParam = FindAreaTreeParam.builder().build();
        }
        List<FindAreaResult> districtList = provinceService.findDistrictNode(null);
        LinkedHashMap<Long, List<FindAreaResult>> districtMap = districtList.stream().collect(Collectors.groupingBy(FindAreaResult::getPid, LinkedHashMap::new, Collectors.toList()));
        List<FindAreaResult> districtListResult = districtMap.get(findAreaTreeParam.getCityId());
        return returnSuccess("查询地区列表成功!", districtListResult);
    }

    /**
     * 处预定规则信息
     *
     * @param districtDTOList
     * @author kj
     * @date 2021-08-20 16:54:56
     */
    private List<FindDistrictResult> processDistrictInfo(List<DistrictDTO> districtDTOList) {
        return districtDTOList.stream().map(districtDTO -> {
            FindDistrictResult findDistrictResult = FindDistrictResult.builder().build();
            BeanUtil.copyProperties(districtDTO, findDistrictResult, CopyOptions.create().ignoreNullValue());
            return findDistrictResult;
        }).collect(Collectors.toList());
    }

    /*
     * 
     * @author kj
     * @date 2021/9/6 17:04  
     * @param [findDistrictParam]
     * @return java.lang.Object
     */
    @RequestMapping("findDistrictSelect")
    @CommonBusiness(logRemark = "查询地区成功")
    @PreAuthorize("hasAuthority('districtAction:findDistrictSelect')")
    public Object findDistrictSelect(@RequestBody(required = false) FindDistrictParam findDistrictParam){
        if (findDistrictParam == null) {
            findDistrictParam = FindDistrictParam.builder().build();
        }
        List<DistrictDTO> districtDTOList = districtService.findDistrictSelect(findDistrictParam);
        List<FindDistrictResult> findDistrictResultList = processDistrictInfo(districtDTOList);
        return returnSuccess("查询地区列表成功!", findDistrictResultList);
    }

    /*
     * 添加地区
     * @author kj
     * @date 2021/8/13 11:34
     * @param [addDistrictParam]
     * @return java.lang.Object
     */
    @RequestMapping("addDistrict")
    @CommonBusiness(logRemark = "创建地区")
    @PreAuthorize("hasAuthority('districtAction:addDistrict')")
    public Object addDistrict(@RequestBody(required = false) AddDistrictParam addDistrictParam) {
        String checkError = checkAddDistrict(addDistrictParam);
        if (StringUtils.isNotBlank(checkError)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, checkError);
        }
        DistrictDO districtDO = DistrictDO.builder()
                .districtId(numberMachineUtils.getTableID(NumberMachineConstants.DISTRICT_TABLE_ID_SEQ))
                .build();
        BeanUtil.copyProperties(addDistrictParam, districtDO, CopyOptions.create().ignoreNullValue());
        try {
            districtDO.setDistrictChar(PinyinUtils.toPinYinUppercase(addDistrictParam.getDistrictName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "全称转换拼音失败!");
        }
        districtService.addDistrict(districtDO);
        districtRedisUtils.updateDistrict(districtDO.getDistrictId());
        return returnSuccess("保存地区成功！");
    }

    /*
     * 根据id查询地区
     * @author kj
     * @date 2021/8/13 11:34
     * @param [queryDistrictParam]
     * @return java.lang.Object
     */
    @RequestMapping("queryDistrictById")
    @CommonBusiness(logRemark = "根据id查询地区")
    @PreAuthorize("hasAuthority('districtAction:queryDistrictById')")
    public Object queryDistrictById(@RequestBody(required = false) QueryDistrictParam queryDistrictParam) {
        if (queryDistrictParam == null || queryDistrictParam.getDistrictId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择查询id!");
        }
        DistrictDO districtDO = districtService.queryDistrictById(queryDistrictParam.getDistrictId());
        QueryDistrictResult queryDistrictResult = QueryDistrictResult.builder().build();
        BeanUtil.copyProperties(districtDO, queryDistrictResult, CopyOptions.create().ignoreNullValue());
        return returnSuccess("根据id查询地区成功!", queryDistrictResult);
    }

    /*
     * 修改地区
     * @author kj
     * @date 2021/8/13 11:34
     * @param [updateDistrictParam]
     * @return java.lang.Object
     */
    @RequestMapping("updateDistrict")
    @CommonBusiness(logRemark = "修改地区")
    @PreAuthorize("hasAuthority('districtAction:updateDistrict')")
    public Object updateDistrict(@RequestBody(required = false) UpdateDistrictParam updateDistrictParam) {
        String checkResult = checkUpdateDistrict(updateDistrictParam);
        if (StringUtils.isNotBlank(checkResult)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, checkResult);
        }
        DistrictDO districtDO = districtService.queryDistrictById(updateDistrictParam.getDistrictId());
        if (districtDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到地区信息!");
        }
        BeanUtil.copyProperties(updateDistrictParam, districtDO, CopyOptions.create().ignoreNullValue());
        try {
            districtDO.setDistrictChar(PinyinUtils.toPinYinUppercase(updateDistrictParam.getDistrictName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "全称转换拼音失败!");
        }
        districtService.updateDistrict(districtDO);
        districtRedisUtils.updateDistrict(districtDO.getDistrictId());
        return returnSuccess("修改地区成功!");
    }

    /*
     * 删除地区
     * @author kj
     * @date 2021/8/13 11:34
     * @param [deleteDistrictParam]
     * @return java.lang.Object
     */
    @RequestMapping("deleteDistrict")
    @CommonBusiness(logRemark = "删除地区")
    @PreAuthorize("hasAuthority('districtAction:deleteDistrict')")
    public Object deleteDistrict(@RequestBody(required = false) DeleteDistrictParam deleteDistrictParam) {
        if (deleteDistrictParam == null || deleteDistrictParam.getDistrictId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择删除地区");
        }

        DistrictDO districtDO = districtService.queryDistrictById(deleteDistrictParam.getDistrictId());
        if (districtDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到地区信息!");
        }

        districtService.deleteDistrict(deleteDistrictParam.getDistrictId());
        districtRedisUtils.deleteDistrict(deleteDistrictParam.getDistrictId().toString());
        return returnSuccess("删除地区成功!");
    }

    /*
     * 修改地区参数校验
     * @author kj
     * @date 2021/8/13 11:35
     * @param [updateDistrictParam]
     * @return java.lang.String
     */
    private String checkUpdateDistrict(UpdateDistrictParam updateDistrictParam) {
        if (updateDistrictParam == null || updateDistrictParam.getDistrictId() == null) {
            return "请选择修改地区!";
        }
        if (StringUtils.isEmpty(updateDistrictParam.getDistrictName())) {
            return "请输入地区名称!";
        }
        if (districtService.checkNameIfExist(updateDistrictParam.getDistrictName(), updateDistrictParam.getDistrictId())) {
            return "地区名称已存在!";
        }
        return null;
    }

    /*
     * 添加地区参数校验
     * @author kj
     * @date 2021/8/13 11:35
     * @param [addDistrictParam]
     * @return java.lang.String
     */
    private String checkAddDistrict(AddDistrictParam addDistrictParam) {
        if (addDistrictParam == null) {
            return "请输入地区信息!";
        }
        if (StringUtils.isEmpty(addDistrictParam.getDistrictName())) {
            return "请输入地区名称!";
        }
        if (districtService.checkNameIfExist(addDistrictParam.getDistrictName(), null)) {
            return "地区名称已存在!";
        }
        if (addDistrictParam.getCertificateNo() == null) {
            return "请输入地区身份证编号";
        }
        return null;
    }

}
