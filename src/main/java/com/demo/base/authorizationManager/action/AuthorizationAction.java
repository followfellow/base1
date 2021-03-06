package com.demo.base.authorizationManager.action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.demo.SequenceNumberBizBean;
import com.demo.action.BaseAction;
import com.demo.action.result.ResultCode;
import com.demo.aop.CommonBusiness;
import com.demo.base.authorizationManager.po.JurisGroupMenuDO;
import com.demo.base.authorizationManager.po.RoleMenuDO;
import com.demo.base.authorizationManager.request.*;
import com.demo.base.authorizationManager.response.FindUserAuthResult;
import com.demo.base.authorizationManager.service.AuthorizationService;
import com.demo.base.jurisGroupManager.dto.JurisGroupDTO;
import com.demo.base.jurisGroupManager.po.JurisGroupDO;
import com.demo.base.jurisGroupManager.service.JurisGroupService;
import com.demo.base.roleManager.dto.RoleDTO;
import com.demo.base.roleManager.po.RoleDO;
import com.demo.base.roleManager.service.RoleService;
import com.demo.base.userManager.dto.UserDTO;
import com.demo.base.userManager.po.UserDO;
import com.demo.base.userManager.service.UserService;
import com.demo.common.tree.TreeBuildFactory;
import com.demo.contants.Constants;
import com.demo.contants.NumberMachineConstants;
import com.demo.system.menuManager.dto.MenuDTO;
import com.demo.system.menuManager.response.FindMenuResult;
import com.demo.utils.JsonUtils;
import com.demo.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限分配 Action
 *
 * @author sw
 * @date 2021-06-19 10:14:27
 */
@RestController
@RequestMapping(Constants.OAPI+"authorizationAction")
@Slf4j
public class AuthorizationAction extends BaseAction {

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private JurisGroupService jurisGroupService;
    @Autowired
    private UserService userService;


    /**
     * 查询当前用户角色
     *
     * @author wxc
     * @date 2021/7/26 13:33
     */
    @RequestMapping("findCurrentUserRole")
    @CommonBusiness(logRemark = "查询当前用户角色")
//   @PreAuthorize("hasAuthority('authorizationAction:findCurrentUserRole')")
    public Object findCurrentUserRole(HttpServletRequest request) {
        UserDTO userDTO = getCurrentUser(UserDTO.class);
        UserDTO curUser = userService.findUserByName(userDTO.getUserName());
        if (curUser == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到用户信息!");
        }
        //设置当前登录角色缓存
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null) {
                String tokenValue = authHeader.replace("bearer", "").trim();
                Object object = redisUtils.get(tokenValue);
                if (object != null) {
                    JSONObject jsonObject = JsonUtils.getJSONObject(object.toString());
                    if (jsonObject == null || StringUtils.isEmpty(jsonObject.getString("userName"))) {
                        return returnFail(ResultCode.AUTH_LOGIN_USER_TIME_OUT, "请重新登录系统!");
                    }
                    jsonObject.put("userRealName", curUser.getUserRealName());
                    jsonObject.put("businessId", curUser.getBusinessId());
                    jsonObject.put("userId", curUser.getUserId());
                    jsonObject.put("businessAllName", curUser.getBusinessAllName());
                    redisUtils.set(tokenValue, jsonObject);
                }
            }
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_LOGIN_USER_TIME_OUT, "请重新登录系统");
        }
        List<RoleDTO> roles = roleService.findRoleByUserId(curUser.getUserId());
        HashMap<String, Object> result = new HashMap<>(4);
        result.put("roles", roles);
        result.put("userInfo", new HashMap<String, Object>() {{
            put("businessAllName", curUser.getBusinessAllName());
            put("userRealName", curUser.getUserRealName());
            put("userName", curUser.getUserName());
        }});
        return returnSuccess("查询当前用户角色成功!", result);
    }


    /**
     * 根据当前用户选择角色查询菜单权限
     *
     * @param findMenuByRoleParam
     * @author wxc
     * @date 2021/7/26 14:43
     */
    @RequestMapping("findMenuByCurrentRole")
    @CommonBusiness(logRemark = "根据当前用户选择角色查询菜单权限")
//   @PreAuthorize("hasAuthority('authorizationAction:findMenuByCurrentRole')")
    public Object findMenuByCurrentUser(@RequestBody(required = false) FindMenuByRoleParam findMenuByRoleParam, HttpServletRequest request) {
        if (findMenuByRoleParam == null || findMenuByRoleParam.getRoleId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择角色!");
        }
        RoleDO roleDO = roleService.queryRoleById(findMenuByRoleParam.getRoleId());
        if (roleDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到角色信息!");
        }
        //设置当前登录角色缓存
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null) {
                String tokenValue = authHeader.replace("bearer", "").trim();
                Object object = redisUtils.get(tokenValue);
                if (object != null) {
                    JSONObject jsonObject = JsonUtils.getJSONObject(object.toString());
                    if (jsonObject == null || StringUtils.isEmpty(jsonObject.getString("userName"))) {
                        return returnFail(ResultCode.AUTH_LOGIN_USER_TIME_OUT, "请重新登录系统!");
                    }
                    jsonObject.put("roleId", roleDO.getRoleId());
                    redisUtils.set(tokenValue, jsonObject);
                }
            }
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_LOGIN_USER_TIME_OUT, "请重新登录系统");
        }
        List<MenuDTO> menuDTOList;
        //系统管理员
        if (roleDO.getRolePid() == 0L) {
            if (roleDO.getBusinessId() == null) {
                return returnFail(ResultCode.AUTH_PARAM_ERROR, "该角色数据异常请联系管理员!");
            }
            JurisGroupDTO jurisGroupDTO = jurisGroupService.findGroupByBusinessId(roleDO.getBusinessId());
            if (jurisGroupDTO == null) {
                return returnFail(ResultCode.BIS_DATA_NO_EXIST, "当前单位暂未添加到分组!");
            }
            //获取当前公司系统管理员权限
            menuDTOList = authorizationService.findMenuByGroup(jurisGroupDTO.getJurisGroupId());
        }
        //普通角色
        else {
            menuDTOList = authorizationService.findMenuByRole(roleDO.getRoleId());
        }
        if (CollectionUtil.isNotEmpty(menuDTOList)) {
            List<FindMenuResult> findMenuResultList = menuDTOList.stream().map(menuDTO -> {
                FindMenuResult findMenuResult = FindMenuResult.builder().build();
                BeanUtil.copyProperties(menuDTO, findMenuResult, CopyOptions.create().ignoreNullValue());
                return findMenuResult;
            }).collect(Collectors.toList());
            return returnSuccess("查询当前角色菜单成功!", new TreeBuildFactory<FindMenuResult>().doTreeBuild(findMenuResultList));
        }
        return returnSuccess("查询当前角色菜单成功!");
    }

    /**
     * 查询用户角色权限
     *
     * @param findMenuInfoByUserParam
     * @author wxc
     * @date 2021/7/26 15:24
     */
    @RequestMapping("findMenuByUser")
    @CommonBusiness(logRemark = "根据用户查询菜单权限信息")
    @PreAuthorize("hasAuthority('authorizationAction:findMenuByUser')")
    public Object findMenuInfoByUser(@RequestBody(required = false) FindMenuInfoByUserParam findMenuInfoByUserParam) {
        if (findMenuInfoByUserParam == null || findMenuInfoByUserParam.getUserId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择查询用户!");
        }
        UserDO userDO = userService.queryUserById(findMenuInfoByUserParam.getUserId());
        if (userDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到用户信息!");
        }
        List<RoleDTO> roleDTOList = roleService.findRoleByUserId(userDO.getUserId());
        if (CollectionUtil.isEmpty(roleDTOList)) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "当前用户尚未绑定角色!");
        }
        if (roleDTOList.stream().anyMatch(roleDTO -> roleDTO.getRolePid() == 0L)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "无法查看系统管理员权限!");
        }
        List<FindUserAuthResult> findUserAuthResults = roleDTOList.stream().map(roleDTO ->
                FindUserAuthResult
                        .builder()
                        .roleName(roleDTO.getRoleName())
                        .findMenuResultList(new TreeBuildFactory<FindMenuResult>().doTreeBuild(authorizationService.findMenuByRole(roleDTO.getRoleId())
                                .stream()
                                .map(menuDTO -> {
                                    FindMenuResult findMenuResult = FindMenuResult.builder().build();
                                    BeanUtil.copyProperties(menuDTO, findMenuResult, CopyOptions.create().ignoreNullValue());
                                    return findMenuResult;
                                })
                                .collect(Collectors.toList())))
                        .build()
        ).collect(Collectors.toList());
        return returnSuccess("查询用户角色权限成功!", findUserAuthResults);
    }

    /**
     * 根据角色id获取菜单权限
     *
     * @param findMenuByRoleParam
     * @author wxc
     * @date 2021/8/16 15:41
     */
    @RequestMapping("findMenuByRole")
    @CommonBusiness(logRemark = "根据角色获取菜单权限")
    @PreAuthorize("hasAuthority('authorizationAction:findMenuByRole')")
    public Object findMenuByRole(@RequestBody(required = false) FindMenuByRoleParam findMenuByRoleParam) {
        if (findMenuByRoleParam == null || findMenuByRoleParam.getRoleId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择查询角色!");
        }
        RoleDO roleDO = roleService.queryRoleById(findMenuByRoleParam.getRoleId());
        if (roleDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到角色信息!");
        }
        List<MenuDTO> menuDTOList;
        //系统管理员
        if (roleDO.getRolePid() == 0L) {
            if (roleDO.getBusinessId() == null) {
                return returnFail(ResultCode.AUTH_PARAM_ERROR, "该角色数据异常请联系管理员!");
            }
            JurisGroupDTO jurisGroupDTO = jurisGroupService.findGroupByBusinessId(roleDO.getBusinessId());
            if (jurisGroupDTO == null) {
                return returnFail(ResultCode.BIS_DATA_NO_EXIST, "当前单位暂未添加到分组!");
            }
            //获取当前公司系统管理员权限
            menuDTOList = authorizationService.findMenuByGroup(jurisGroupDTO.getJurisGroupId());
        }
        //普通角色
        else {
            menuDTOList = authorizationService.findMenuByRole(roleDO.getRoleId());
        }
        if (CollectionUtil.isNotEmpty(menuDTOList)) {
            List<FindMenuResult> findMenuResultList = menuDTOList.stream().map(menuDTO -> {
                FindMenuResult findMenuResult = FindMenuResult.builder().build();
                BeanUtil.copyProperties(menuDTO, findMenuResult, CopyOptions.create().ignoreNullValue());
                return findMenuResult;
            }).collect(Collectors.toList());
            return returnSuccess("查询当前角色菜单成功!", new TreeBuildFactory<FindMenuResult>().doTreeBuild(findMenuResultList));
        }
        return returnFail(ResultCode.AUTH_INSUFFICIENT_USER_PERMISSIONS, "当前角色暂无权限!");
    }


    /**
     * 根据角色id获取菜单权限勾选回显
     *
     * @param findMenuByRoleParam
     * @author:sw
     * @date 2021/6/19 16:22
     */
    @RequestMapping("findMenuIdsByRole")
    @CommonBusiness(logRemark = "根据角色id获取菜单权限勾选回显")
    @PreAuthorize("hasAuthority('authorizationAction:findMenuIdsByRole')")
    public Object findMenuIdsByRole(@RequestBody(required = false) FindMenuByRoleParam findMenuByRoleParam) {
        if (findMenuByRoleParam == null || findMenuByRoleParam.getRoleId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择查询角色!");
        }
        RoleDO roleDO = roleService.queryRoleById(findMenuByRoleParam.getRoleId());
        if (roleDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到角色信息!");
        }
        List<Long> menuIds = authorizationService.findMenuByRole(findMenuByRoleParam.getRoleId()).stream().map(MenuDTO::getMenuId).collect(Collectors.toList());
        return returnSuccess("获取菜单权限勾选回显成功!", menuIds);
    }

    /**
     * 根据分组获取菜单权限
     *
     * @param findMenuByJurisGroupParam
     * @author wxc
     * @date 2021/8/16 15:43
     */
    @RequestMapping("findMenuByGroup")
    @CommonBusiness(logRemark = "根据分组获取菜单权限")
    @PreAuthorize("hasAuthority('authorizationAction:findMenuByGroup')")
    public Object findMenuByGroup(@RequestBody(required = false) FindMenuByJurisGroupParam findMenuByJurisGroupParam) {
        if (findMenuByJurisGroupParam == null || findMenuByJurisGroupParam.getJurisGroupId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择分组!");
        }
        JurisGroupDO jurisGroupDO = jurisGroupService.queryJurisGroupById(findMenuByJurisGroupParam.getJurisGroupId());
        if (jurisGroupDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到分组信息!");
        }
        List<MenuDTO> menuDTOList = authorizationService.findMenuByGroup(jurisGroupDO.getJurisGroupId());
        if (CollectionUtil.isNotEmpty(menuDTOList)) {
            List<FindMenuResult> findMenuResultList = menuDTOList.stream().map(menuDTO -> {
                FindMenuResult findMenuResult = FindMenuResult.builder().build();
                BeanUtil.copyProperties(menuDTO, findMenuResult, CopyOptions.create().ignoreNullValue());
                return findMenuResult;
            }).collect(Collectors.toList());
            return returnSuccess("查询当前分组权限成功!", new TreeBuildFactory<FindMenuResult>().doTreeBuild(findMenuResultList));
        }
        return returnFail(ResultCode.AUTH_INSUFFICIENT_USER_PERMISSIONS, "当前分组暂无权限!");
    }

    /**
     * 根据分组id获取菜单权限勾选回显
     *
     * @param findMenuByJurisGroupParam
     * @author wxc
     * @date 2021/7/26 15:38
     */
    @RequestMapping("findMenuIdsByGroup")
    @CommonBusiness(logRemark = "根据分组id获取菜单权限勾选回显")
    @PreAuthorize("hasAuthority('authorizationAction:findMenuIdsByGroup')")
    public Object findMenuIdsByGroup(@RequestBody(required = false) FindMenuByJurisGroupParam findMenuByJurisGroupParam) {
        if (findMenuByJurisGroupParam == null || findMenuByJurisGroupParam.getJurisGroupId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择分组!");
        }
        JurisGroupDO jurisGroupDO = jurisGroupService.queryJurisGroupById(findMenuByJurisGroupParam.getJurisGroupId());
        if (jurisGroupDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到分组信息!");
        }
        List<Long> menuIds = authorizationService.findMenuByGroup(jurisGroupDO.getJurisGroupId()).stream().map(MenuDTO::getMenuId).collect(Collectors.toList());
        return returnSuccess("获取菜单权限勾选回显成功!", menuIds);
    }


    /**
     * 操作角色菜单授权
     *
     * @param operateRoleMenuParam
     * @param request
     * @return
     * @author:sw
     * @date 2021/6/24 10:43
     */
    @RequestMapping("operateRoleMenu")
    @CommonBusiness(logRemark = "操作角色菜单授权")
    @PreAuthorize("hasAuthority('authorizationAction:operateRoleMenu')")
    public Object operateRoleMenu(@RequestBody(required = false) OperateRoleMenuParam operateRoleMenuParam, HttpServletRequest request) {
        if (operateRoleMenuParam == null || operateRoleMenuParam.getRoleId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择操作角色");
        }
        List<String> menuNoList = operateRoleMenuParam.getMenuNoList();
        if (CollectionUtil.isEmpty(menuNoList)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择操作菜单!");
        }
        //验证角色是否存在
        RoleDO roleDO = roleService.queryRoleById(operateRoleMenuParam.getRoleId());
        if (roleDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到角色信息!");
        }
        //系统管理员权限无法修改
        if (roleDO.getRolePid() == 0L) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "系统管理员权限无法修改!");
        }
        UserDTO userDTO = getCurrentUser(UserDTO.class);
        if (userDTO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "当前登录角色异常!");
        }
        RoleDO currentRole = roleService.queryRoleById(userDTO.getRoleId());
        if (currentRole == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到当前登录角色信息!");
        }
        List<MenuDTO> menuDTOList;
        //系统管理员
        if (currentRole.getRolePid() == 0L) {
            if (currentRole.getBusinessId() == null) {
                return returnFail(ResultCode.AUTH_PARAM_ERROR, "该角色数据异常请联系管理员!");
            }
            JurisGroupDTO jurisGroupDTO = jurisGroupService.findGroupByBusinessId(currentRole.getBusinessId());
            if (jurisGroupDTO == null) {
                return returnFail(ResultCode.BIS_DATA_NO_EXIST, "当前单位暂未添加到分组!");
            }
            //获取当前公司系统管理员权限
            menuDTOList = authorizationService.findMenuByGroup(jurisGroupDTO.getJurisGroupId());
        }
        //普通角色
        else {
            menuDTOList = authorizationService.findMenuByRole(currentRole.getRoleId());
        }
        //验证当前登录用户是否有操作的菜单权限
        if (CollectionUtil.isEmpty(menuDTOList)) {
            return returnFail(ResultCode.AUTH_INSUFFICIENT_USER_PERMISSIONS, "暂无权限!");
        }
        List<String> currUserMenuNoList = menuDTOList.stream().map(MenuDTO::getMenuNo).collect(Collectors.toList());
        for (String menuNo : menuNoList) {
            if (!currUserMenuNoList.contains(menuNo)) {
                log.info("请求唯一标识：" + getReqid(request) + ";当前用户没有该菜单权限，菜单权限编号:" + menuNo + ";");
                return returnFail(ResultCode.AUTH_INSUFFICIENT_USER_PERMISSIONS, "暂无权限!");
            }
        }
        //组合角色菜单权限
        SequenceNumberBizBean sequenceNumberBizBean = numberMachineUtils.getTableIDByCount(NumberMachineConstants.ROLE_MENU_TABLE_ID_SEQ, menuNoList.size());
        List<RoleMenuDO> roleMenuDOList = menuNoList.stream()
                .map(menuNo -> RoleMenuDO.builder()
                        .roleMenuId(numberMachineUtils.getTableIdBySequenceNumber(sequenceNumberBizBean))
                        .roleId(operateRoleMenuParam.getRoleId())
                        .menuNo(menuNo)
                        .businessId(roleDO.getBusinessId())
                        .build())
                .collect(Collectors.toList());
        authorizationService.operateRoleMenu(operateRoleMenuParam.getRoleId(), roleMenuDOList);
        return returnSuccess("保存成功!");
    }

    /**
     * 操作分组菜单授权
     *
     * @param operateGroupMenuParam
     * @param request
     * @author wxc
     * @date 2021/7/28 11:12
     */
    @RequestMapping("operateGroupMenu")
    @CommonBusiness(logRemark = "操作分组菜单授权")
    @PreAuthorize("hasAuthority('authorizationAction:operateGroupMenu')")
    public Object operateGroupMenu(@RequestBody(required = false) OperateGroupMenuParam operateGroupMenuParam, HttpServletRequest request) {
        if (operateGroupMenuParam == null || operateGroupMenuParam.getJurisGroupId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择操作分组!");
        }
        List<String> menuNoList = operateGroupMenuParam.getMenuNoList();
        if (CollectionUtil.isEmpty(menuNoList)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择操作菜单!");
        }
        //验证分组是否存在
        JurisGroupDO jurisGroupDO = jurisGroupService.queryJurisGroupById(operateGroupMenuParam.getJurisGroupId());
        if (jurisGroupDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到分组信息!");
        }
        if (jurisGroupDO.getJurisGroupPid() == 0L) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "不能修改系统管理员权限!");
        }
        //验证当前登录角色权限
        UserDTO userDTO = getCurrentUser(UserDTO.class);
        if (userDTO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "当前登录角色异常!");
        }
        RoleDO currentRole = roleService.queryRoleById(userDTO.getRoleId());
        if (currentRole == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到当前登录角色信息!");
        }
        List<MenuDTO> menuDTOList;
        //系统管理员
        if (currentRole.getRolePid() == 0L) {
            if (currentRole.getBusinessId() == null) {
                return returnFail(ResultCode.AUTH_PARAM_ERROR, "该角色数据异常请联系管理员!");
            }
            JurisGroupDTO jurisGroupDTO = jurisGroupService.findGroupByBusinessId(currentRole.getBusinessId());
            if (jurisGroupDTO == null) {
                return returnFail(ResultCode.BIS_DATA_NO_EXIST, "当前单位暂未添加到分组!");
            }
            //获取当前公司系统管理员权限
            menuDTOList = authorizationService.findMenuByGroup(jurisGroupDTO.getJurisGroupId());
        }
        //普通角色
        else {
            menuDTOList = authorizationService.findMenuByRole(currentRole.getRoleId());
        }
        //验证当前登录用户是否有操作的菜单权限
        if (CollectionUtil.isEmpty(menuDTOList)) {
            return returnFail(ResultCode.AUTH_INSUFFICIENT_USER_PERMISSIONS, "暂无权限!");
        }
        List<String> currUserMenuNoList = menuDTOList.stream().map(MenuDTO::getMenuNo).collect(Collectors.toList());
        for (String menuNo : menuNoList) {
            if (!currUserMenuNoList.contains(menuNo)) {
                log.info("请求唯一标识：" + getReqid(request) + ";当前用户没有该菜单权限，菜单权限编号:" + menuNo + ";");
                return returnFail(ResultCode.AUTH_INSUFFICIENT_USER_PERMISSIONS, "暂无权限!");
            }
        }
        //deleteMenuNos 需要删除的权限
        List<String> deleteMenuNos;
        //addMenuNos 需要新增的权限
        List<String> addMenuNos;
        //旧权限
        List<String> oldMenuNos = authorizationService.findMenuByGroup(operateGroupMenuParam.getJurisGroupId()).stream().map(MenuDTO::getMenuNo).collect(Collectors.toList());
        //筛选新增和删除的菜单编号
        if (CollectionUtil.isEmpty(oldMenuNos)) {
            deleteMenuNos = null;
            addMenuNos = menuNoList;
        } else {
            deleteMenuNos = oldMenuNos.stream().filter(i -> !menuNoList.contains(i)).collect(Collectors.toList());
            addMenuNos = menuNoList.stream().filter(i -> !oldMenuNos.contains(i)).collect(Collectors.toList());
        }
        //组合分组菜单权限
        SequenceNumberBizBean sequenceNumberBizBean = numberMachineUtils.getTableIDByCount(NumberMachineConstants.SYS_JURIS_GROUP_MENU_TABLE_ID_SEQ, addMenuNos.size());
        List<JurisGroupMenuDO> addJurisGroupMenuList = addMenuNos.stream()
                .map(menuNo -> JurisGroupMenuDO.builder()
                        .jurisGroupId(operateGroupMenuParam.getJurisGroupId())
                        .menuNo(menuNo)
                        .jurisGroupMenuId(numberMachineUtils.getTableIdBySequenceNumber(sequenceNumberBizBean))
                        .build())
                .collect(Collectors.toList());
        authorizationService.operateGroupMenu(operateGroupMenuParam.getJurisGroupId(), addJurisGroupMenuList, deleteMenuNos);
        return returnSuccess("保存成功!");
    }

}
