package com.demo.base.countryManager.action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.demo.action.BaseAction;
import com.demo.action.result.ResultCode;
import com.demo.action.vo.QueryPage;
import com.demo.aop.CommonBusiness;
import com.demo.base.countryManager.dto.CountryDTO;
import com.demo.base.countryManager.po.CountryDO;
import com.demo.base.countryManager.request.*;
import com.demo.base.countryManager.response.FindCountryResult;
import com.demo.base.countryManager.response.QueryCountryResult;
import com.demo.base.countryManager.service.CountryService;
import com.demo.cache.country.CountryRedisUtils;
import com.demo.contants.Constants;
import com.demo.contants.NumberMachineConstants;
import com.demo.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 国家action
 *
 * @author kj
 * @date 2021/8/10 16:17
 */
@RestController
@RequestMapping("countryAction")
//@RequestMapping(Constants.OAPI+"countryAction")
public class CountryAction extends BaseAction {

    @Autowired
    private CountryService countryService;
    @Autowired
    private CountryRedisUtils countryRedisUtils;

    /*
     * 查询国家列表
     * @author kj
     * @date 2021/8/11 10:31
     * @param [findCountryParam]
     * @return java.lang.Object
     */
    @RequestMapping("findCountryList")
    @CommonBusiness(logRemark = "查询国家成功")
//    @PreAuthorize("hasAuthority('userAction:findCountryList')")
    public Object findCountryList(@RequestBody(required = false) FindCountryParam findCountryParam) {
        if (findCountryParam == null) {
            findCountryParam = FindCountryParam.builder().build();
        }
        QueryPage queryPage = initQueryPage(findCountryParam);
        List<CountryDTO> countryDTOList = countryService.findCountryList(findCountryParam, queryPage);
        List<FindCountryResult> findCountryResultList = processCountryInfo(countryDTOList);
        return returnSuccessListByPage(findCountryResultList, queryPage, "查询国家列表成功!");
    }

    /**
     * 处预定规则信息
     *
     * @param countryDTOList
     * @author kj
     * @date 2021-08-20 16:54:56
     */
    private List<FindCountryResult> processCountryInfo(List<CountryDTO> countryDTOList) {
        return countryDTOList.stream().map(countryDTO -> {
            FindCountryResult findCountryResult = FindCountryResult.builder().build();
            BeanUtil.copyProperties(countryDTO, findCountryResult, CopyOptions.create().ignoreNullValue());
            return findCountryResult;
        }).collect(Collectors.toList());
    }

    /*
     * 添加国家
     * @author kj
     * @date 2021/8/12 9:24
     * @param [addCountryParam]
     * @return java.lang.Object
     */
    @RequestMapping("addCountry")
    @CommonBusiness(logRemark = "创建国家")
//    @PreAuthorize("hasAuthority('userAction:addCountry')")
    public Object addCountry(@RequestBody(required = false) AddCountryParam addCountryParam){
        String checkError = checkAddCountry(addCountryParam);
        if (StringUtils.isNotBlank(checkError)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, checkError);
        }
        CountryDO countryDO=CountryDO.builder()
                .countryId(numberMachineUtils.getTableID(NumberMachineConstants.COUNTRY_TABLE_ID_SEQ))
                .build();
        BeanUtil.copyProperties(addCountryParam, countryDO, CopyOptions.create().ignoreNullValue());
        countryService.addCountry(countryDO);
        countryRedisUtils.updateCountry(countryDO.getCountryId());
        return returnSuccess("保存国家成功！");
    }

    /*
     * 根据id查询国家
     * @author kj
     * @date 2021/8/11 17:38
     * @param [queryCountryParam]
     * @return java.lang.Object
     */
    @RequestMapping("queryCountryById")
    @CommonBusiness(logRemark = "根据id查询国家")
//    @PreAuthorize("hasAuthority('userAction:queryCountryById')")
    public Object queryCountryById(@RequestBody(required = false) QueryCountryParam queryCountryParam) {
        if (queryCountryParam == null || queryCountryParam.getCountryId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择查询id!");
        }
        CountryDO countryDO = countryService.queryCountryById(queryCountryParam.getCountryId());
        QueryCountryResult queryCountryResult = QueryCountryResult.builder().build();
        BeanUtil.copyProperties(countryDO, queryCountryResult, CopyOptions.create().ignoreNullValue());
        return returnSuccess("根据id查询国家成功!", queryCountryResult);
    }

    /*
     * 修改国家
     * @author kj
     * @date 2021/8/12 10:29
     * @param [updateCountryParam]
     * @return java.lang.Object
     */
    @RequestMapping("updateCountry")
    @CommonBusiness(logRemark = "修改国家")
//    @PreAuthorize("hasAuthority('userAction:updateCountry')")
    public Object updateCountry(@RequestBody(required = false) UpdateCountryParam updateCountryParam) {
        String checkResult = checkUpdateCountry(updateCountryParam);
        if (StringUtils.isNotBlank(checkResult)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, checkResult);
        }
        CountryDO countryDO = countryService.queryCountryById(updateCountryParam.getCountryId());
        if (countryDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到国家信息!");
        }

        BeanUtil.copyProperties(updateCountryParam, countryDO, CopyOptions.create().ignoreNullValue());
        countryService.updateCountry(countryDO);
        countryRedisUtils.updateCountry(countryDO.getCountryId());
        return returnSuccess("修改国家成功!");
    }

    /*
     * 删除国家
     * @author kj
     * @date 2021/8/12 10:29
     * @param [deleteCountryParam]
     * @return java.lang.Object
     */
    @RequestMapping("deleteCountry")
    @CommonBusiness(logRemark = "删除国家")
//    @PreAuthorize("hasAuthority('userAction:deleteCountry')")
    public Object deleteCountry(@RequestBody(required = false) DeleteCountryParam deleteCountryParam) {
        if (deleteCountryParam == null || deleteCountryParam.getCountryId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择删除国家");
        }

        CountryDO countryDO = countryService.queryCountryById(deleteCountryParam.getCountryId());
        if (countryDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到国家信息!");
        }
        countryService.deleteCountry(deleteCountryParam.getCountryId());

        BeanUtil.copyProperties(countryDO, deleteCountryParam, CopyOptions.create().ignoreNullValue());
        countryRedisUtils.deleteCountry(deleteCountryParam.getCountryId().toString());
        return returnSuccess("删除国家成功!");
    }

    /*
     * 修改国家参数校验
     * @author kj
     * @date 2021/8/12 10:29
     * @param [updateCountryParam]
     * @return java.lang.String
     */
    private String checkUpdateCountry(UpdateCountryParam updateCountryParam) {
        if (updateCountryParam == null || updateCountryParam.getCountryId() == null) {
            return "请选择修改国家!";
        }
        if (StringUtils.isEmpty(updateCountryParam.getCountryName())) {
            return "请输入国家名称!";
        }
        if (countryService.checkNameIfExist(updateCountryParam.getCountryName(), updateCountryParam.getCountryId())) {
            return "国家名称已存在!";
        }
        return null;
    }

    /*
     * 添加国家参数校验
     * @author kj
     * @date 2021/8/12 10:28
     * @param [addCountryParam]
     * @return java.lang.String
     */
    private String checkAddCountry(AddCountryParam addCountryParam) {
        if (addCountryParam == null) {
            return "请输入国家信息!";
        }
        if (StringUtils.isEmpty(addCountryParam.getCountryName())) {
            return "请输入国家名称!";
        }
        if (countryService.checkNameIfExist(addCountryParam.getCountryName(), null)) {
            return "国家名称已存在!";
        }
        return null;
    }
}
