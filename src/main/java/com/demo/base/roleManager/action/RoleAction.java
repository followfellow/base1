package com.demo.base.roleManager.action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.demo.action.BaseAction;
import com.demo.action.result.ResultCode;
import com.demo.aop.CommonBusiness;
import com.demo.base.roleManager.po.RoleDO;
import com.demo.base.roleManager.service.RoleService;
import com.demo.base.roleManager.dto.RoleDTO;
import com.demo.base.roleManager.request.AddRoleParam;
import com.demo.base.roleManager.request.DeleteRoleParam;
import com.demo.base.roleManager.request.QueryRoleParam;
import com.demo.base.roleManager.request.UpdateRoleParam;
import com.demo.base.roleManager.response.FindRoleResult;
import com.demo.base.roleManager.response.QueryRoleResult;
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

/**
 * 角色 Action
 *
 * @author sw
 * @date 2021-06-13 17:30:58
 */
@RestController
@RequestMapping(Constants.OAPI+"roleAction")
@Slf4j
public class RoleAction extends BaseAction {

    @Autowired
    private RoleService roleService;

    /**
     * 查询角色列表
     *
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    @RequestMapping("findRoleList")
    @CommonBusiness(logRemark = "查询角色树")
    @PreAuthorize("hasAuthority('roleAction:findRoleList')")
    public Object findRoleList() {
        List<RoleDTO> roleList = roleService.findRoleList();
        List<FindRoleResult> findRoleResultList = roleList.stream().map(roleDTO -> {
            FindRoleResult findRoleResult = FindRoleResult.builder().build();
            BeanUtil.copyProperties(roleDTO, findRoleResult, CopyOptions.create().ignoreNullValue());
            return findRoleResult;
        }).collect(Collectors.toList());
        findRoleResultList = new TreeBuildFactory<FindRoleResult>().doTreeBuild(findRoleResultList);
        return returnSuccess("查询角色树成功!", findRoleResultList);
    }


    /**
     * 添加角色
     *
     * @param addRoleParam
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    @RequestMapping("addRole")
    @CommonBusiness(logRemark = "创建角色")
    @PreAuthorize("hasAuthority('roleAction:addRole')")
    public Object addRole(@RequestBody(required = false) AddRoleParam addRoleParam) {
        String checkError = checkAddRole(addRoleParam);
        if (StringUtils.isNotBlank(checkError)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, checkError);
        }
        roleService.addRole(RoleDO.builder()
                .roleId(numberMachineUtils.getTableID(NumberMachineConstants.ROLE_TABLE_ID_SEQ))
                .rolePid(addRoleParam.getRolePid())
                .roleName(addRoleParam.getRoleName())
                .businessId(getCurrUserOrgId()).build());
        return returnSuccess("保存角色成功!");
    }


    /**
     * 根据 id 查询 角色
     *
     * @param queryRoleParam
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    @RequestMapping("queryRoleById")
    @CommonBusiness(logRemark = "根据id查询角色")
    @PreAuthorize("hasAuthority('roleAction:queryRoleById')")
    public Object queryRoleById(@RequestBody(required = false) QueryRoleParam queryRoleParam) {
        if (queryRoleParam == null || queryRoleParam.getRoleId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择查询角色!");
        }
        RoleDO roleDO = roleService.queryRoleById(queryRoleParam.getRoleId());
        QueryRoleResult queryRoleResult = QueryRoleResult.builder().build();
        BeanUtil.copyProperties(roleDO, queryRoleResult, CopyOptions.create().ignoreNullValue());
        return returnSuccess("根据id查询角色成功!", queryRoleResult);
    }


    /**
     * 修改角色
     *
     * @param updateRoleParam
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    @RequestMapping("updateRole")
    @CommonBusiness(logRemark = "修改角色")
    @PreAuthorize("hasAuthority('roleAction:updateRole')")
    public Object updateRole(@RequestBody(required = false) UpdateRoleParam updateRoleParam) {
        String checkResult = checkUpdateRole(updateRoleParam);
        if (StringUtils.isNotBlank(checkResult)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, checkResult);
        }
        RoleDO roleDO = roleService.queryRoleById(updateRoleParam.getRoleId());
        if (roleDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到角色信息!");
        }
        if (roleDO.getRolePid() == 0L) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "不能修改系统管理员!");
        }
        BeanUtil.copyProperties(updateRoleParam, roleDO, CopyOptions.create().ignoreNullValue());
        roleService.updateRole(roleDO);
        return returnSuccess("修改角色成功!");
    }

    /**
     * 删除角色
     *
     * @param deleteRoleParam
     * @return
     * @author sw
     * @date 2021-06-13 17:30:58
     */
    @RequestMapping("deleteRole")
    @CommonBusiness(logRemark = "删除角色")
    @PreAuthorize("hasAuthority('roleAction:deleteRole')")
    public Object deleteRole(@RequestBody(required = false) DeleteRoleParam deleteRoleParam) {
        if (deleteRoleParam == null || deleteRoleParam.getRoleId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择删除角色");
        }

        RoleDO roleDO = roleService.queryRoleById(deleteRoleParam.getRoleId());
        if (roleDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到角色信息!");
        }
        if (roleDO.getRolePid() == 0L) {
            return returnFail(ResultCode.ERROR, "不能删除系统管理员!");
        }
        if (roleService.checkIfHasChild(roleDO.getRoleId())) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请先删除下级角色!");
        }
        if (roleService.checkUserIfExist(roleDO.getRoleId())) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "该角色已分配用户，无法删除!");
        }
        roleService.deleteRole(deleteRoleParam.getRoleId());
        return returnSuccess("删除角色成功!");
    }


    /**
     * 修改角色参数校验
     *
     * @param updateRoleParam
     * @author wxc
     * @date 2021/8/9 13:39
     */
    private String checkUpdateRole(UpdateRoleParam updateRoleParam) {
        //修改必须传入节点roleId
        if (updateRoleParam == null || updateRoleParam.getRoleId() == null) {
            return "请选择修改角色!";
        }
        if (StringUtils.isEmpty(updateRoleParam.getRoleName())) {
            return "请输入角色名称!";
        }
        if (roleService.checkNameIfExist(updateRoleParam.getRoleName(), updateRoleParam.getRoleId())) {
            return "角色名称已存在!";
        }
        return null;
    }

    /**
     * 添加角色参数校验
     *
     * @param addRoleParam
     * @author wxc
     * @date 2021/8/9 13:36
     */
    private String checkAddRole(AddRoleParam addRoleParam) {
        if (addRoleParam == null) {
            return "请输入角色信息!";
        }
        //新增必须依附某个父级节点
        if (addRoleParam.getRolePid() == null) {
            return "请选择上级角色!";
        }
        if (addRoleParam.getRolePid() == 0L) {
            return "无法添加系统管理员角色!";
        }
        if (StringUtils.isEmpty(addRoleParam.getRoleName())) {
            return "请输入角色名称!";
        }
        if (roleService.checkNameIfExist(addRoleParam.getRoleName(), null)) {
            return "角色名称已存在!";
        }
        return null;
    }
}
