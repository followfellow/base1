package com.demo.base.jurisGroupManager.action;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.demo.action.BaseAction;
import com.demo.action.result.ResultCode;
import com.demo.aop.CommonBusiness;
import com.demo.base.jurisGroupManager.dto.JurisGroupDTO;
import com.demo.base.jurisGroupManager.po.JurisGroupDO;
import com.demo.base.jurisGroupManager.request.AddJurisGroupParam;
import com.demo.base.jurisGroupManager.request.DeleteJurisGroupParam;
import com.demo.base.jurisGroupManager.request.QueryJurisGroupParam;
import com.demo.base.jurisGroupManager.request.UpdateJurisGroupParam;
import com.demo.base.jurisGroupManager.response.FindJurisGroupResult;
import com.demo.base.jurisGroupManager.response.QueryJurisGroupResult;
import com.demo.base.jurisGroupManager.service.JurisGroupService;
import com.demo.common.tree.TreeBuildFactory;
import com.demo.contants.Constants;
import com.demo.contants.NumberMachineConstants;
import com.demo.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

;


/**
 * JurisGroupAction
 *
 * @author:wxc
 * @date:2021-07-23 17:03:23
 */

@RestController
@RequestMapping(Constants.OAPI+"jurisGroupAction")
@Slf4j
public class JurisGroupAction extends BaseAction {

    @Autowired
    private JurisGroupService jurisGroupService;


    /**
     * 查询分组
     *
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    @RequestMapping("findJurisGroupList")
    @CommonBusiness(logRemark = "查询分组")
    @PreAuthorize("hasAuthority('jurisGroupAction:findJurisGroupList')")
    public Object findJurisGroupList() {
        List<JurisGroupDTO> jurisGroupList = jurisGroupService.findJurisGroupList();
        List<FindJurisGroupResult> jurisGroupResultList = jurisGroupList.stream().map(jurisGroupDTO -> {
            FindJurisGroupResult findJurisGroupResult = FindJurisGroupResult.builder().build();
            BeanUtil.copyProperties(jurisGroupDTO, findJurisGroupResult, CopyOptions.create().ignoreNullValue());
            return findJurisGroupResult;
        }).collect(Collectors.toList());
        jurisGroupResultList = new TreeBuildFactory<FindJurisGroupResult>().doTreeBuild(jurisGroupResultList);
        return returnSuccess("查询分组成功!", jurisGroupResultList);
    }


    /**
     * 根据id查询分组
     *
     * @param queryJurisGroupParam
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    @RequestMapping("queryJurisGroupById")
    @CommonBusiness(logRemark = "根据id查询分组")
    @PreAuthorize("hasAuthority('jurisGroupAction:queryJurisGroupById')")
    public Object queryJurisGroupById(@RequestBody(required = false) QueryJurisGroupParam queryJurisGroupParam) {
        if (queryJurisGroupParam == null || queryJurisGroupParam.getJurisGroupId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择查询分组!");
        }
        JurisGroupDO jurisGroupDO = jurisGroupService.queryJurisGroupById(queryJurisGroupParam.getJurisGroupId());
        QueryJurisGroupResult queryJurisGroupResult = QueryJurisGroupResult.builder().build();
        BeanUtil.copyProperties(jurisGroupDO, queryJurisGroupResult);
        return returnSuccess("查询分组成功!", queryJurisGroupResult);
    }


    /**
     * 添加分组
     *
     * @param addJurisGroupParam
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    @RequestMapping("addJurisGroup")
    @CommonBusiness(logRemark = "添加分组")
    @PreAuthorize("hasAuthority('jurisGroupAction:addJurisGroup')")
    public Object addJurisGroup(@RequestBody(required = false) AddJurisGroupParam addJurisGroupParam) {
        String resultError = checkAddJurisGroupParam(addJurisGroupParam);
        if (!StringUtils.isEmpty(resultError)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, resultError);
        }
        jurisGroupService.addJurisGroup(JurisGroupDO.builder()
                .jurisGroupId(numberMachineUtils.getTableID(NumberMachineConstants.SYS_JURIS_GROUP_TABLE_ID_SEQ))
                .jurisGroupName(addJurisGroupParam.getJurisGroupName())
                .jurisGroupPid(addJurisGroupParam.getJurisGroupPid())
                .build());
        return returnSuccess("添加分组成功!");
    }


    /**
     * 修改分组
     *
     * @param updateJurisGroupParam
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    @RequestMapping("updateJurisGroup")
    @CommonBusiness(logRemark = "修改分组")
    @PreAuthorize("hasAuthority('jurisGroupAction:updateJurisGroup')")
    public Object updateJurisGroup(@RequestBody(required = false) UpdateJurisGroupParam updateJurisGroupParam) {
        String resultError = checkUpdateJurisGroupParam(updateJurisGroupParam);
        if (!StringUtils.isEmpty(resultError)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, resultError);
        }
        JurisGroupDO jurisGroupDO = jurisGroupService.queryJurisGroupById(updateJurisGroupParam.getJurisGroupId());
        if (jurisGroupDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到分组信息!");
        }
        if (jurisGroupDO.getJurisGroupPid() == 0L) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "系统管理员分组信息不能修改!");
        }
        jurisGroupDO.setJurisGroupName(updateJurisGroupParam.getJurisGroupName());
        jurisGroupService.updateJurisGroup(jurisGroupDO);
        return returnSuccess("修改分组成功!");
    }


    /**
     * 删除分组
     *
     * @param deleteJurisGroupParam
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    @RequestMapping("deleteJurisGroup")
    @CommonBusiness(logRemark = "删除分组")
    @PreAuthorize("hasAuthority('jurisGroupAction:deleteJurisGroup')")
    public Object deleteJurisGroup(@RequestBody(required = false) DeleteJurisGroupParam deleteJurisGroupParam) {
        if (deleteJurisGroupParam == null || deleteJurisGroupParam.getJurisGroupId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择删除分组");
        }
        JurisGroupDO jurisGroupDO = jurisGroupService.queryJurisGroupById(deleteJurisGroupParam.getJurisGroupId());
        if (jurisGroupDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到分组信息!");
        }
        if (jurisGroupDO.getJurisGroupPid() == 0L) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "系统管理员分组不能删除!");
        }
        if (jurisGroupService.checkChildGroupIfExist(jurisGroupDO.getJurisGroupId())) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请先删除下级分组!");
        }
        if(jurisGroupService.checkBusinessIfExist(jurisGroupDO.getJurisGroupId())){
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "该分组已分配单位，无法删除!");
        }
        jurisGroupService.deleteJurisGroup(deleteJurisGroupParam.getJurisGroupId());
        return returnSuccess("删除分组成功!");
    }

    /**
     * 校验新增或者修改数据
     *
     * @param updateJurisGroupParam
     * @author wxc
     * @date 2021-07-23 17:03:23
     */
    private String checkUpdateJurisGroupParam(UpdateJurisGroupParam updateJurisGroupParam) {
        if (updateJurisGroupParam == null || updateJurisGroupParam.getJurisGroupId() == null) {
            return "请选择修改分组!";
        }
        if (StringUtils.isEmpty(updateJurisGroupParam.getJurisGroupName())) {
            return "请输入分组名称!";
        }
        if (jurisGroupService.checkNameIfExist(updateJurisGroupParam.getJurisGroupName(), updateJurisGroupParam.getJurisGroupId())) {
            return "分组名称已存在!";
        }
        return null;
    }

    /**
     * 添加分组参数校验
     *
     * @param addJurisGroupParam
     * @author wxc
     * @date 2021/8/9 15:34
     */
    private String checkAddJurisGroupParam(AddJurisGroupParam addJurisGroupParam) {
        if (addJurisGroupParam == null) {
            return "请输入分组信息!";
        }
        if (addJurisGroupParam.getJurisGroupPid() == null) {
            return "请选择上级分组!";
        }
        if (StringUtils.isEmpty(addJurisGroupParam.getJurisGroupName())) {
            return "请输入分组名称!";
        }
        if (jurisGroupService.checkNameIfExist(addJurisGroupParam.getJurisGroupName(), null)) {
            return "分组名称已存在!";
        }
        return null;
    }

}