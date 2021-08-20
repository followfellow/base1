package com.demo.base.userManager.action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import com.demo.SequenceNumberBizBean;
import com.demo.action.BaseAction;
import com.demo.action.result.ResultCode;
import com.demo.action.vo.QueryPage;
import com.demo.aop.CommonBusiness;
import com.demo.base.roleManager.po.RoleDO;
import com.demo.base.roleManager.service.RoleService;
import com.demo.base.userManager.po.RoleJoinUserDO;
import com.demo.base.userManager.po.UserDO;
import com.demo.base.roleManager.dto.RoleDTO;
import com.demo.base.userManager.request.*;
import com.demo.base.userManager.service.UserService;
import com.demo.base.userManager.dto.UserDTO;
import com.demo.base.userManager.response.FindUserListResult;
import com.demo.base.userManager.response.QueryUserResult;
import com.demo.contants.CodeConstants;
import com.demo.contants.Constants;
import com.demo.contants.NumberMachineConstants;
import com.demo.utils.StringUtils;
import com.demo.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户 Action
 *
 * @author sw
 * @date 2021-06-13 09:36:21
 */
@RestController
@RequestMapping("userAction")
@Slf4j
public class UserAction extends BaseAction {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 查询用户列表
     *
     * @param findUserParam
     * @return
     * @author sw
     * @date 2021-06-13 09:36:21
     */
    @RequestMapping("findUserList")
    @CommonBusiness(logRemark = "查询用户列表")
    public Object findUserList(@RequestBody(required = false) FindUserParam findUserParam) {
        if (findUserParam == null) {
            findUserParam = FindUserParam.builder().build();
        }
        QueryPage queryPage = initQueryPage(findUserParam);
        List<UserDTO> userList = userService.findUserList(findUserParam, queryPage);
        List<FindUserListResult> findUserListResultList = processUserInfo(userList);
        return returnSuccessListByPage(findUserListResultList, queryPage, "查询用户列表成功!");
    }

    /**
     * 处理用户信息封装
     *
     * @param userList
     * @author wxc
     * @date 2021/8/7 16:52
     */
    private List<FindUserListResult> processUserInfo(List<UserDTO> userList) {
        if (CollectionUtil.isNotEmpty(userList)) {
            List<Long> userIdList = userList.stream().map(UserDTO::getUserId).collect(Collectors.toList());
            //获取用户对应角色
            List<RoleDTO> roleList = userService.findRoleByUserIds(userIdList);
            //select b.country_id,b.country,group_concat(a.city separator '/'),city,max(a.city_id) ,group_concat(a.city_id separator '/') from  city a,country b where a.country_id = b.country_id and b.country_id in(1,2,3,5,9,15)  group by b.country_id
            //可以在数据库查询的时候使用group_concat() 来实现直接拼接，无需走java
            Map<Long, String> roleNameMap = roleList.stream()
                    .collect(Collectors.groupingBy(RoleDTO::getUserId))
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(RoleDTO::getRoleName).collect(Collectors.joining("/"))));
            return userList.stream()
                    .map(userDTO -> {
                        FindUserListResult findUserListResult = FindUserListResult.builder().roleName(roleNameMap.get(userDTO.getUserId())).build();
                        BeanUtil.copyProperties(userDTO, findUserListResult, CopyOptions.create().ignoreNullValue());
                        String validTime = findUserListResult.getValidTime();
                        if (StringUtils.isNotBlank(validTime)) {
                            findUserListResult.setValidTime("-1".equals(validTime) ? "无限期" : validTime.replace(",", "至"));
                        }
                        return findUserListResult;
                    })
                    .collect(Collectors.toList());
        }
        return null;
    }


    /**
     * 添加用户
     *
     * @param addUserParam
     * @return
     * @author sw
     * @date 2021-06-13 09:36:21
     */
    @RequestMapping("addUser")
    @CommonBusiness(logRemark = "添加用户")
    public Object addUser(@RequestBody(required = false) AddUserParam addUserParam) {
        String resultError = checkAddUserParam(addUserParam);
        if (StringUtils.isNotBlank(resultError)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, resultError);
        }
        UserDO userDO = UserDO.builder()
                .userId(numberMachineUtils.getTableID(NumberMachineConstants.USER_TABLE_ID_SEQ))
                .businessId(getCurrUserOrgId())
                .flag(Constants.COMMON_ONE)
                .userIdentity(CodeConstants.USER_IDENTITY_PTYH)
                .build();
        BeanUtil.copyProperties(addUserParam, userDO, CopyOptions.create().ignoreNullValue());
        userDO.setPassword(passwordEncoder.encode(addUserParam.getPassword()));
        userService.addUser(userDO);
        return returnSuccess("保存用户成功!");
    }


    /**
     * 根据id查询用户
     *
     * @param queryUserParam
     * @return
     * @author sw
     * @date 2021-06-13 09:36:21
     */
    @RequestMapping("queryUserById")
    @CommonBusiness(logRemark = "根据id查询用户")
    public Object queryUserById(@RequestBody(required = false) QueryUserParam queryUserParam) {
        if (queryUserParam == null || queryUserParam.getUserId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择查询用户!");
        }
        UserDO userDO = userService.queryUserById(queryUserParam.getUserId());
        QueryUserResult queryUserResult = QueryUserResult.builder().build();
        BeanUtil.copyProperties(userDO, queryUserResult, CopyOptions.create().ignoreNullValue());
        return returnSuccess("查询用户成功!", queryUserResult);
    }


    /**
     * 修改用户
     *
     * @param updateUserParam
     * @return
     * @author sw
     * @date 2021-06-13 09:36:21
     */
    @RequestMapping("updateUser")
    @CommonBusiness(logRemark = "修改用户")
    public Object updateUser(@RequestBody(required = false) UpdateUserParam updateUserParam) {
        String resultError = checkUpdateUserParam(updateUserParam);
        if (!StringUtils.isEmpty(resultError)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, resultError);
        }
        UserDO userDO = userService.queryUserById(updateUserParam.getUserId());
        if (userDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到用户信息!");
        }
        BeanUtil.copyProperties(updateUserParam, userDO, CopyOptions.create().ignoreNullValue());
        userService.updateUser(userDO);
        return returnSuccess("修改用户成功!");
    }

    /**
     * 修改用户状态
     *
     * @param modifyUserStatusParam
     * @author wxc
     * @date 2021/7/20 19:08
     */
    @RequestMapping("modifyUserStatus")
    @CommonBusiness(logRemark = "修改用户状态")
    public Object modifyUserStatus(@RequestBody(required = false) ModifyUserStatusParam modifyUserStatusParam) {
        if (modifyUserStatusParam == null || modifyUserStatusParam.getUserId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择修改用户!");
        }
        UserDO userDO = userService.queryUserById(modifyUserStatusParam.getUserId());
        if (userDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到用户信息!");
        }
        if (Constants.COMMON_ONE.equals(userDO.getFlag())) {
            modifyUserStatusParam.setFlag(Constants.COMMON_TWO);
        } else {
            modifyUserStatusParam.setFlag(Constants.COMMON_ONE);
        }
        userService.modifyUserStatus(modifyUserStatusParam);
        return returnSuccess("修改用户状态成功!");
    }

    /**
     * 重置密码
     *
     * @param resetPasswordParam
     * @author wxc
     * @date 2021/7/20 19:18
     */
    @RequestMapping("resetPassword")
    @CommonBusiness(logRemark = "重置密码")
    public Object resetPassword(@RequestBody(required = false) ResetPasswordParam resetPasswordParam) {
        if (resetPasswordParam == null || resetPasswordParam.getUserId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择用户!");
        }
        if (StringUtils.isEmpty(resetPasswordParam.getPassword())) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请生成新密码!");
        }
        UserDO userDO = userService.queryUserById(resetPasswordParam.getUserId());
        if (userDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到用户信息!");
        }
        resetPasswordParam.setPassword(passwordEncoder.encode(resetPasswordParam.getPassword()));
        //重置密码为随机六位数,由前台生成
        userService.resetPassword(resetPasswordParam);
        return returnSuccess("重置密码成功!");
    }

    /**
     * 删除用户
     *
     * @param deleteUserParam
     * @return
     * @author sw
     * @date 2021-06-13 09:36:21
     */
    @RequestMapping("deleteUser")
    @CommonBusiness(logRemark = "删除用户")
    public Object deleteUser(@RequestBody(required = false) DeleteUserParam deleteUserParam) {
        if (deleteUserParam == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择删除用户");
        }
        if (deleteUserParam.getUserId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择删除用户!");
        }
        UserDO userDO = userService.queryUserById(deleteUserParam.getUserId());
        if (userDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到用户信息!");
        }
        userService.deleteUser(deleteUserParam);
        return returnSuccess("删除用户成功!");
    }


    /**
     * 添加角色用户
     *
     * @author:sw
     * @date 2021/6/24 20:04
     **/
    @RequestMapping("operateRoleUser")
    @CommonBusiness(logRemark = "添加角色用户")
    public Object operateRoleUser(@RequestBody OperateRoleUserParam operateRoleUserParam) {
        if (operateRoleUserParam == null || operateRoleUserParam.getRoleId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择角色!");
        }
        List<Long> userIdList = operateRoleUserParam.getUserIdList();
        if (CollectionUtil.isEmpty(userIdList)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择用户!");
        }
        RoleDO roleDO = roleService.queryRoleById(operateRoleUserParam.getRoleId());
        if (roleDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到角色信息!");
        }
        if (roleDO.getRolePid() == 0L) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "无法查看系统管理员用户信息!");
        }
        List<RoleJoinUserDO> roleJoinUserDOList = new ArrayList<>();
        SequenceNumberBizBean sequenceNumberBizBean = numberMachineUtils.getTableIDByCount(NumberMachineConstants.ROLE_JOIN_USER_TABLE_ID_SEQ, userIdList.size());
        userIdList.forEach(userId -> roleJoinUserDOList.add(RoleJoinUserDO.builder()
                .roleId(operateRoleUserParam.getRoleId())
                .businessId(roleDO.getBusinessId())
                .userId(userId)
                .roleJoinUserId(numberMachineUtils.getTableIdBySequenceNumber(sequenceNumberBizBean))
                .build()));
        userService.operateRoleUser(roleJoinUserDOList);
        return returnSuccess("保存成功!");
    }

    /**
     * 查询角色用户列表
     *
     * @param findRoleUserParam
     * @return
     * @author:sw
     * @date 2021/6/24 20:14
     */
    @RequestMapping("findRoleUser")
    @CommonBusiness(logRemark = "查询角色用户列表")
    public Object findRoleUser(@RequestBody FindRoleUserParam findRoleUserParam) {
        if (findRoleUserParam == null || findRoleUserParam.getRoleId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择角色!");
        }
        RoleDO roleDO = roleService.queryRoleById(findRoleUserParam.getRoleId());
        if (roleDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到角色信息!");
        }
        if (roleDO.getRolePid() == 0L) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "无法查看超级管理员用户信息!");
        }
        QueryPage queryPage = initQueryPage(findRoleUserParam);
        List<UserDTO> userDTOList = userService.findRoleUser(findRoleUserParam, queryPage);
        List<FindUserListResult> findUserListResultList = processUserInfo(userDTOList);
        return returnSuccessListByPage(findUserListResultList, queryPage, "查询角色用户列表成功!");
    }

    /**
     * 删除角色用户绑定
     *
     * @param deleteUserRoleParam
     * @author wxc
     * @date 2021/7/28 19:19
     */
    @RequestMapping("deleteRoleUser")
    @CommonBusiness(logRemark = "删除角色用户")
    public Object deleteRoleUser(@RequestBody DeleteUserRoleParam deleteUserRoleParam) {
        if (deleteUserRoleParam == null || deleteUserRoleParam.getRoleId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择角色!");
        }
        if (deleteUserRoleParam.getUserId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择用户!");
        }
        userService.deleteRoleUser(deleteUserRoleParam);
        return returnSuccess("删除角色用户成功!");
    }

    /**
     * 查询非该角色用户列表
     *
     * @param findRoleUserParam
     * @author wxc
     * @date 2021/7/29 9:09
     */
    @RequestMapping("findNoRoleUser")
    @CommonBusiness(logRemark = "查询非该角色用户列表")
    public Object findNoRoleUser(@RequestBody FindRoleUserParam findRoleUserParam) {
        if (findRoleUserParam == null || findRoleUserParam.getRoleId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择角色!");
        }
        QueryPage queryPage = initQueryPage(findRoleUserParam);
        RoleDO roleDO = roleService.queryRoleById(findRoleUserParam.getRoleId());
        if (roleDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到角色信息!");
        }
        if (roleDO.getRolePid() == 0L) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "不能添加超级管理员用户!");
        }
        List<UserDTO> userDTOList = userService.findNoRoleUser(findRoleUserParam, queryPage);
        List<FindUserListResult> findUserListResultList = processUserInfo(userDTOList);
        return returnSuccessListByPage(findUserListResultList, queryPage, "查询非该角色用户列表成功!");
    }


    /**
     * 修改用户参数校验
     *
     * @param updateUserParam
     * @author wxc
     * @date 2021/8/7 14:58
     */
    private String checkUpdateUserParam(UpdateUserParam updateUserParam) {
        if (updateUserParam == null) {
            return "请输入用户信息!";
        }
        if (updateUserParam.getUserId() == null) {
            return "请选择修改用户!";
        }
        if (StringUtils.isEmpty(updateUserParam.getUserRealName())) {
            return "请输入用户姓名!";
        }
        if (StringUtils.isEmpty(updateUserParam.getValidTime())) {
            return "请选择用户有效期限!";
        }
        if (!StringUtils.isEmpty(updateUserParam.getCellphone())) {
            if (!ValidatorUtils.checkMobilPhone(updateUserParam.getCellphone())) {
                return "【" + updateUserParam.getCellphone() + "】手机号码不合法!";
            }
        }
        return null;
    }

    /**
     * 新增用户参数校验
     *
     * @param addUserParam
     * @author wxc
     * @date 2021/8/7 14:56
     */
    private String checkAddUserParam(AddUserParam addUserParam) {
        if (addUserParam == null) {
            return "请输入用户信息!";
        }
        if (StringUtils.isEmpty(addUserParam.getUserName())) {
            return "请输入登录账号!";
        }
        if (StringUtils.isEmpty(addUserParam.getPassword())) {
            return "请输入密码!";
        }
        if (StringUtils.isEmpty(addUserParam.getUserRealName())) {
            return "请输入用户姓名!";
        }
        if (!StringUtils.isEmpty(addUserParam.getCellphone())) {
            if (!ValidatorUtils.checkMobilPhone(addUserParam.getCellphone())) {
                return "【" + addUserParam.getCellphone() + "】手机号码不合法!";
            }
        }
        if (StringUtils.isEmpty(addUserParam.getValidTime())) {
            return "请选择用户有效期限!";
        }
        //验证登录账户是否已经存在
        if (userService.checkUserName(addUserParam.getUserName())) {
            return "登录账号已存在!";
        }
        return null;

    }


}
