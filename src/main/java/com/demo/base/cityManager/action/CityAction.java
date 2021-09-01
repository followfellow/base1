package com.demo.base.cityManager.action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.demo.action.BaseAction;
import com.demo.action.result.ResultCode;
import com.demo.aop.CommonBusiness;
import com.demo.base.cityManager.dto.CityDTO;
import com.demo.base.cityManager.po.CityDO;
import com.demo.base.cityManager.request.*;
import com.demo.base.cityManager.response.FindCityResult;
import com.demo.base.cityManager.response.QueryCityResult;
import com.demo.base.cityManager.service.CityService;
import com.demo.cache.city.CityRedisUtils;
import com.demo.contants.NumberMachineConstants;
import com.demo.utils.PinyinUtils;
import com.demo.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * 城市action
 * @author kj
 * @date 2021/8/12 18:32
 */
@RestController
@RequestMapping("cityAction")
public class CityAction extends BaseAction {

    @Autowired
    private CityService cityService;
    @Autowired
    private CityRedisUtils cityRedisUtils;

    /*
     * 查询城市列表
     * @author kj
     * @date 2021/8/12 18:52
     * @param [findCityParam, request]
     * @return java.lang.Object
     */
    @RequestMapping("findCityList")
    @CommonBusiness(logRemark = "查询城市成功")
    public Object findCityList(@RequestBody(required = false) FindCityParam findCityParam) {


        if (findCityParam == null) {
            findCityParam = FindCityParam.builder().build();
        }
        List<CityDTO> cityDTOList = cityService.findCityList(findCityParam);
        List<FindCityResult> findCityResultList = processCityInfo(cityDTOList);

        int size = findCityResultList.size();
        FindCityResult build = FindCityResult.builder()
                .findCityResultList(findCityResultList)
                .citySize(size)
                .build();

//        return returnSuccessListByPage(findCityResultList, queryPage, "查询城市列表成功!");
        return returnSuccess("查询城市列表成功!",build );
    }

    /**
     * 处预定规则信息
     *
     * @param cityDTOList
     * @author kj
     * @date 2021-08-20 16:54:56
     */
    private List<FindCityResult> processCityInfo(List<CityDTO> cityDTOList) {
        return cityDTOList.stream().map(cityDTO -> {
            FindCityResult findCityResult = FindCityResult.builder().build();
            BeanUtil.copyProperties(cityDTO, findCityResult, CopyOptions.create().ignoreNullValue());
            return findCityResult;
        }).collect(Collectors.toList());
    }

    /*
     * 添加城市
     * @author kj
     * @date 2021/8/13 11:34  
     * @param [addCityParam]
     * @return java.lang.Object
     */
    @RequestMapping("addCity")
    @CommonBusiness(logRemark = "创建城市")
    public Object addCity(@RequestBody(required = false) AddCityParam addCityParam){
        String checkError = checkAddCity(addCityParam);
        if (StringUtils.isNotBlank(checkError)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, checkError);
        }
        CityDO cityDO=CityDO.builder()
                .cityId(numberMachineUtils.getTableID(NumberMachineConstants.CITY_TABLE_ID_SEQ))
                .build();
        BeanUtil.copyProperties(addCityParam, cityDO, CopyOptions.create().ignoreNullValue());
        try {
            cityDO.setCityChar(PinyinUtils.toPinYinUppercase(addCityParam.getCityName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR,  "全称转换拼音失败!");
        }
        cityService.addCity(cityDO);
        cityRedisUtils.updateCity(cityDO.getCityId());
        return returnSuccess("保存城市成功！");
    }

    /*
     * 根据id查询城市
     * @author kj
     * @date 2021/8/13 11:34  
     * @param [queryCityParam]
     * @return java.lang.Object
     */
    @RequestMapping("queryCityById")
    @CommonBusiness(logRemark = "根据id查询城市")
    public Object queryCityById(@RequestBody(required = false) QueryCityParam queryCityParam) {
        if (queryCityParam == null || queryCityParam.getCityId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择查询id!");
        }
        CityDO cityDO = cityService.queryCityById(queryCityParam.getCityId());
        QueryCityResult queryCityResult = QueryCityResult.builder().build();
        BeanUtil.copyProperties(cityDO, queryCityResult, CopyOptions.create().ignoreNullValue());
        return returnSuccess("根据id查询城市成功!", queryCityResult);
    }

    /*
     * 修改城市
     * @author kj
     * @date 2021/8/13 11:34  
     * @param [updateCityParam]
     * @return java.lang.Object
     */
    @RequestMapping("updateCity")
    @CommonBusiness(logRemark = "修改城市")
    public Object updateCity(@RequestBody(required = false) UpdateCityParam updateCityParam) {
        String checkResult = checkUpdateCity(updateCityParam);
        if (StringUtils.isNotBlank(checkResult)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, checkResult);
        }
        CityDO cityDO = cityService.queryCityById(updateCityParam.getCityId());
        if (cityDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到城市信息!");
        }
        BeanUtil.copyProperties(updateCityParam, cityDO, CopyOptions.create().ignoreNullValue());
        try {
            cityDO.setCityChar(PinyinUtils.toPinYinUppercase(updateCityParam.getCityName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR,  "全称转换拼音失败!");
        }
        cityService.updateCity(cityDO);
        cityRedisUtils.updateCity(cityDO.getCityId());
        return returnSuccess("修改城市成功!");
    }

    /*
     * 删除城市
     * @author kj
     * @date 2021/8/13 11:34  
     * @param [deleteCityParam]
     * @return java.lang.Object
     */
    @RequestMapping("deleteCity")
    @CommonBusiness(logRemark = "删除城市")
    public Object deleteCity(@RequestBody(required = false) DeleteCityParam deleteCityParam) {
        if (deleteCityParam == null || deleteCityParam.getCityId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择删除城市");
        }

        CityDO cityDO = cityService.queryCityById(deleteCityParam.getCityId());
        if (cityDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到城市信息!");
        }

        cityService.deleteCity(deleteCityParam.getCityId());
        cityRedisUtils.deleteCity(deleteCityParam.getCityId().toString());
        return returnSuccess("删除城市成功!");
    }
    
    /*
     * 修改城市参数校验
     * @author kj
     * @date 2021/8/13 11:35  
     * @param [updateCityParam]
     * @return java.lang.String
     */
    private String checkUpdateCity(UpdateCityParam updateCityParam) {
        if (updateCityParam == null || updateCityParam.getCityId() == null) {
            return "请选择修改城市!";
        }
        if (StringUtils.isEmpty(updateCityParam.getCityName())) {
            return "请输入城市名称!";
        }
        if (cityService.checkNameIfExist(updateCityParam.getCityName(), updateCityParam.getCityId())) {
            return "城市名称已存在!";
        }
        return null;
    }

    /*
     * 添加城市参数校验
     * @author kj
     * @date 2021/8/13 11:35  
     * @param [addCityParam]
     * @return java.lang.String
     */
    private String checkAddCity(AddCityParam addCityParam) {
        if (addCityParam == null) {
            return "请输入城市信息!";
        }
        if (StringUtils.isEmpty(addCityParam.getCityName())) {
            return "请输入城市名称!";
        }
        if (cityService.checkNameIfExist(addCityParam.getCityName(), null)) {
            return "城市名称已存在!";
        }
        if (addCityParam.getCertificateNo() == null) {
            return "请输入城市身份证编号";
        }
        return null;
    }

}
