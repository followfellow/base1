package com.demo.base.companyManager.action;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import com.demo.SequenceNumberBizBean;
import com.demo.action.BaseAction;
import com.demo.action.result.ResultCode;
import com.demo.action.vo.QueryPage;
import com.demo.aop.CommonBusiness;
import com.demo.base.companyManager.dto.BusinessDTO;
import com.demo.base.companyManager.po.BusinessDO;
import com.demo.base.companyManager.po.JurisGroupOrgDO;
import com.demo.base.companyManager.request.*;
import com.demo.base.companyManager.result.FindBusinessResult;
import com.demo.base.companyManager.result.QueryBusinessResult;
import com.demo.base.companyManager.service.BusinessService;
import com.demo.base.jurisGroupManager.po.JurisGroupDO;
import com.demo.base.jurisGroupManager.request.FindGroupBusinessParam;
import com.demo.base.jurisGroupManager.service.JurisGroupService;
import com.demo.base.roleManager.po.RoleDO;
import com.demo.base.userManager.po.RoleJoinUserDO;
import com.demo.base.userManager.po.UserDO;
import com.demo.base.userManager.service.UserService;
import com.demo.contants.CodeConstants;
import com.demo.contants.Constants;
import com.demo.contants.NumberMachineConstants;
import com.demo.dbutils.BaseApplicationDO;
import com.demo.utils.PinyinUtils;
import com.demo.utils.StringUtils;
import com.demo.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 单位管理action
 *
 * @author:wxc
 * @date:2021/7/22 15:52
 */
@RestController
@RequestMapping(Constants.OAPI+"businessAction")
@Slf4j
public class BusinessAction extends BaseAction {

    @Autowired
    private BusinessService businessService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private JurisGroupService jurisGroupService;

    /**
     * 查询单位列表
     *
     * @param findBusinessParam
     * @author wxc
     * @date 2021/7/23 10:03
     */
    @RequestMapping("findBusinessList")
    @CommonBusiness(logRemark = "查询单位列表")
   //@PreAuthorize("hasAuthority('businessAction:findBusinessList')")
    public Object findBusinessList(@RequestBody(required = false) FindBusinessParam findBusinessParam) {
        if (findBusinessParam == null || findBusinessParam.getBusinessType() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择单位类型!");
        }
        if (Constants.COMMON_ZERO.equals(findBusinessParam.getBusinessType())) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "无权查看运营商!");
        }
        String tip = switchTip(findBusinessParam.getBusinessType());
        //不是目的地的查询条件将目的地类型置为空
        if (!Constants.COMMON_ONE.equals(findBusinessParam.getBusinessType())) {
            findBusinessParam.setDestinationType(null);
        }
        QueryPage queryPage = initQueryPage(findBusinessParam);
        List<BusinessDTO> businessDTOList = businessService.findBusinessList(findBusinessParam, queryPage);
        List<FindBusinessResult> findBusinessResultList = processBusinessInfo(businessDTOList);
        return returnSuccessListByPage(findBusinessResultList, queryPage, "查询" + tip + "列表成功!");
    }

    /**
     * 获取单位下拉框数据
     *
     * @param findBusinessSelectParam
     * @author wxc
     * @date 2021/8/2 14:23
     */
    @RequestMapping("findBusinessSelect")
    @CommonBusiness(logRemark = "获取单位下拉框数据")
   //@PreAuthorize("hasAuthority('businessAction:findBusinessSelect')")
    public Object findBusinessSelect(@RequestBody(required = false) FindBusinessSelectParam findBusinessSelectParam) {
        if (findBusinessSelectParam == null || findBusinessSelectParam.getBusinessType() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择单位类型!");
        }
        if (Constants.COMMON_ZERO.equals(findBusinessSelectParam.getBusinessType())) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "无权查看运营商!");
        }
        return returnSuccess("获取单位下拉框数据成功！", businessService.findBusinessSelect(findBusinessSelectParam));
    }


    /**
     * 查询商家用户列表
     *
     * @param findMerchantUserParam
     * @author wxc
     * @date 2021/7/29 13:56
     */
    @RequestMapping("findBusinessUserList")
    @CommonBusiness(logRemark = "查询商家用户列表")
   //@PreAuthorize("hasAuthority('businessAction:findBusinessUserList')")
    public Object findBusinessUserList(@RequestBody(required = false) FindMerchantUserParam findMerchantUserParam) {
        if (findMerchantUserParam == null) {
            findMerchantUserParam = FindMerchantUserParam.builder().build();
        }
        if (Constants.COMMON_ZERO.equals(findMerchantUserParam.getBusinessType())) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "无权查看运营商!");
        }
        QueryPage queryPage = initQueryPage(findMerchantUserParam);
        List<BusinessDTO> businessDTOList = businessService.findBusinessUserList(findMerchantUserParam, queryPage);
        List<FindBusinessResult> findBusinessResultList = processBusinessInfo(businessDTOList);
        return returnSuccessListByPage(findBusinessResultList, queryPage, "查询商家用户列表成功!");
    }

    /**
     * 处理单位信息
     *
     * @param businessDTOList
     * @author wxc
     * @date 2021/8/9 18:39
     */
    private List<FindBusinessResult> processBusinessInfo(List<BusinessDTO> businessDTOList) {
        return businessDTOList.stream().map(businessDTO -> {
            FindBusinessResult findBusinessResult = FindBusinessResult.builder().build();
            BeanUtil.copyProperties(businessDTO, findBusinessResult, CopyOptions.create().ignoreNullValue());
            findBusinessResult.setValidTime("-1".equals(businessDTO.getValidTime()) ? "无期限" : businessDTO.getValidTime().replace(",", "至"));
            return findBusinessResult;
        }).collect(Collectors.toList());
    }

    /**
     * 查询商家用户详情
     *
     * @param queryBusinessParam
     * @author wxc
     * @date 2021/7/29 13:57
     */
    @RequestMapping("findBusinessUserInfo")
    @CommonBusiness(logRemark = "查询商家用户详情")
   //@PreAuthorize("hasAuthority('businessAction:findBusinessUserInfo')")
    public Object findBusinessUserInfo(@RequestBody(required = false) QueryBusinessParam queryBusinessParam) {
        if (queryBusinessParam == null || queryBusinessParam.getBusinessId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择商家用户!");
        }
        BusinessDTO businessDTO = businessService.findBusinessUserInfo(queryBusinessParam.getBusinessId());
        businessDTO.setValidTime("-1".equals(businessDTO.getValidTime()) ? "无期限" : businessDTO.getValidTime().replace(",", "至"));
        return returnSuccess("查询商家用户详情成功!", businessDTO);
    }


    /**
     * 查询运营商
     *
     * @param
     * @author wxc
     * @date 2021/7/24 9:29
     */
    @RequestMapping("findOperation")
    @CommonBusiness(logRemark = "查询运营商")
   //@PreAuthorize("hasAuthority('businessAction:findOperation')")
    public Object findOperation() {
        return returnSuccess("查询运营商成功!", businessService.findOperation());
    }

    /**
     * 添加分组单位
     *
     * @param operateGroupBusinessParam
     * @author wxc
     * @date 2021/7/29 9:50
     */
    @RequestMapping("operateGroupBusiness")
    @CommonBusiness(logRemark = "添加分组单位")
   //@PreAuthorize("hasAuthority('businessAction:operateGroupBusiness')")
    public Object operateGroupBusiness(@RequestBody(required = false) OperateGroupBusinessParam operateGroupBusinessParam) {
        if (operateGroupBusinessParam == null || operateGroupBusinessParam.getJurisGroupId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择分组!");
        }
        List<Long> businessIdList = operateGroupBusinessParam.getBusinessIdList();
        if (CollectionUtil.isEmpty(businessIdList)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择单位!");
        }
        JurisGroupDO jurisGroupDO = jurisGroupService.queryJurisGroupById(operateGroupBusinessParam.getJurisGroupId());
        if (jurisGroupDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到分组信息!");
        }
        if (jurisGroupDO.getJurisGroupPid() == 0L) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "系统管理员组无法添加单位!");
        }
        List<JurisGroupOrgDO> jurisGroupOrgDOList = new ArrayList<>();
        SequenceNumberBizBean sequenceNumberBizBean = numberMachineUtils.getTableIDByCount(NumberMachineConstants.SYS_JURIS_GROUP_ORG_TABLE_ID_SEQ, businessIdList.size());
        businessIdList.forEach(businessId -> jurisGroupOrgDOList.add(JurisGroupOrgDO.builder()
                .jurisGroupId(operateGroupBusinessParam.getJurisGroupId())
                .businessId(businessId)
                .jurisGroupOrgId(numberMachineUtils.getTableIdBySequenceNumber(sequenceNumberBizBean))
                .build()));
        businessService.operateGroupBusiness(jurisGroupOrgDOList);
        return returnSuccess("保存成功!");
    }

    /**
     * 查询分组单位列表
     *
     * @param findGroupBusinessParam
     * @author wxc
     * @date 2021/7/29 9:50
     */
    @RequestMapping("findGroupBusiness")
    @CommonBusiness(logRemark = "查询分组单位列表")
   //@PreAuthorize("hasAuthority('businessAction:findGroupBusiness')")
    public Object findGroupBusiness(@RequestBody(required = false) FindGroupBusinessParam findGroupBusinessParam) {
        if (findGroupBusinessParam == null || findGroupBusinessParam.getJurisGroupId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择分组!");
        }
        JurisGroupDO jurisGroupDO = jurisGroupService.queryJurisGroupById(findGroupBusinessParam.getJurisGroupId());
        if (jurisGroupDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到分组信息!");
        }
        if (jurisGroupDO.getJurisGroupPid() == 0L) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "无法查看系统管理员分组信息!");
        }
        //如果不是目的地 则目的地类型清除
        if (findGroupBusinessParam.getBusinessType() != null && !findGroupBusinessParam.getBusinessType().equals(Constants.COMMON_ONE)) {
            if (Constants.COMMON_ZERO.equals(findGroupBusinessParam.getBusinessType())) {
                return returnFail(ResultCode.AUTH_PARAM_ERROR, "无权查看运营商!");
            }
            findGroupBusinessParam.setDestinationType(null);
        }
        QueryPage queryPage = initQueryPage(findGroupBusinessParam);
        List<BusinessDTO> businessDTOList = businessService.findGroupBusiness(findGroupBusinessParam, queryPage);
        return returnSuccessListByPage(businessDTOList, queryPage, "查询分组单位列表!");
    }

    /**
     * 删除单位分组
     *
     * @param deleteGroupBusinessParam
     * @author wxc
     * @date 2021/7/29 9:50
     */
    @RequestMapping("deleteGroupBusiness")
    @CommonBusiness(logRemark = "删除单位分组")
   //@PreAuthorize("hasAuthority('businessAction:deleteGroupBusiness')")
    public Object deleteGroupBusiness(@RequestBody(required = false) DeleteGroupBusinessParam deleteGroupBusinessParam) {
        if (deleteGroupBusinessParam == null || deleteGroupBusinessParam.getJurisGroupId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择分组!");
        }
        if (deleteGroupBusinessParam.getBusinessId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择单位!");
        }
        businessService.deleteGroupBusiness(deleteGroupBusinessParam);
        return returnSuccess("删除单位分组成功!");
    }

    /**
     * 查询未分组单位列表
     *
     * @param findGroupBusinessParam
     * @author wxc
     * @date 2021/7/29 9:51
     */
    @RequestMapping("findNeverInGroupBusiness")
    @CommonBusiness(logRemark = "查询未分组单位列表")
   //@PreAuthorize("hasAuthority('businessAction:findNeverInGroupBusiness')")
    public Object findNeverInGroupBusiness(@RequestBody(required = false) FindGroupBusinessParam findGroupBusinessParam) {
        if (findGroupBusinessParam == null) {
            findGroupBusinessParam = FindGroupBusinessParam.builder().build();
        }
        QueryPage queryPage = initQueryPage(findGroupBusinessParam);
        //如果不是目的地 则目的地类型清除
        if (findGroupBusinessParam.getBusinessType() != null && !findGroupBusinessParam.getBusinessType().equals(Constants.COMMON_ONE)) {
            if (Constants.COMMON_ZERO.equals(findGroupBusinessParam.getBusinessType())) {
                return returnFail(ResultCode.AUTH_PARAM_ERROR, "无权查看运营商!");
            }
            findGroupBusinessParam.setDestinationType(null);
        }
        List<BusinessDTO> businessDTOList = businessService.findNeverInGroupBusiness(findGroupBusinessParam, queryPage);
        return returnSuccessListByPage(businessDTOList, queryPage, "查询未分组单位列表成功!");
    }


    /**
     * 修改运营商
     *
     * @param modifyOperationParam
     * @author wxc
     * @date 2021/7/24 9:29
     */
    @RequestMapping("modifyOperation")
    @CommonBusiness(logRemark = "修改运营商")
   //@PreAuthorize("hasAuthority('businessAction:modifyOperation')")
    public Object modifyOperation(@RequestBody(required = false) ModifyOperationParam modifyOperationParam) {
        String checkResult = checkModifyOperation(modifyOperationParam);
        if (StringUtils.isNotBlank(checkResult)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, checkResult);
        }
        BusinessDO businessDO = businessService.queryBusinessById(modifyOperationParam.getBusinessId());
        if (businessDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到运营商信息!");
        }
        if (!Constants.COMMON_ZERO.equals(businessDO.getBusinessType())) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "单位类型必须是运营商!");
        }
        BeanUtil.copyProperties(modifyOperationParam, businessDO, CopyOptions.create().ignoreNullValue());
        try {
            businessDO.setBusinessAllNamePY(PinyinUtils.toPinYinLowercase(modifyOperationParam.getBusinessAllName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "运营商全称转换拼音失败!");
        }
        try {
            businessDO.setBusinessShortNamePY(PinyinUtils.toPinYinLowercase(modifyOperationParam.getBusinessShortName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "运营商简称转换拼音失败!");
        }
        businessService.updateBusiness(businessDO);
        return returnSuccess("修改运营商成功!");
    }

    /**
     * 修改运营商参数校验
     *
     * @param modifyOperationParam
     * @author wxc
     * @date 2021/8/9 19:16
     */
    private String checkModifyOperation(ModifyOperationParam modifyOperationParam) {
        if (modifyOperationParam == null || modifyOperationParam.getBusinessId() == null) {
            return "请选择运营商!";
        }

        if (StringUtils.isEmpty(modifyOperationParam.getBusinessAllName())) {
            return "请输入运营商全称!";
        }

        if (StringUtils.isEmpty(modifyOperationParam.getBusinessShortName())) {
            return "请输入运营商简称!";
        }

        if (StringUtils.isEmpty(modifyOperationParam.getContactPhoneNo())) {
            return "请输入手机号码!";
        }
        if (!ValidatorUtils.checkMobilPhone(modifyOperationParam.getContactPhoneNo())) {
            return "【" + modifyOperationParam.getContactPhoneNo() + "】手机号码不合法!";
        }
        if (StringUtils.isEmpty(modifyOperationParam.getPlatformName())) {
            return "请输平台名称!";
        }
        if (StringUtils.isEmpty(modifyOperationParam.getPlatformLogo())) {
            return "请上传平台LOGO!";
        }
        if (StringUtils.isEmpty(modifyOperationParam.getMallName())) {
            return "请输入商城名称!";
        }
        if (StringUtils.isEmpty(modifyOperationParam.getMallLogo())) {
            return "请上传商城LOGO!";
        }
        return null;
    }


    /**
     * 添加单位
     *
     * @param addBusinessParam
     * @author wxc
     * @date 2021/7/23 10:03
     */
    @RequestMapping("addBusiness")
    @CommonBusiness(logRemark = "添加单位")
   //@PreAuthorize("hasAuthority('businessAction:addBusiness')")
    public Object addBusiness(@RequestBody(required = false) AddBusinessParam addBusinessParam) {
        String checkResult = checkAddBusinessParam(addBusinessParam);
        if (!StringUtils.isEmpty(checkResult)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, checkResult);
        }
        String tip = switchTip(addBusinessParam.getBusinessType());
        List<BaseApplicationDO> baseApplicationDOList = new ArrayList<>();
        BusinessDO businessDO = BusinessDO.builder()
                .businessId(numberMachineUtils.getTableID(NumberMachineConstants.ORG_BUSINESS_TABLE_ID_SEQ))
                //-------------待修改
                .areaNameStr("上海市")
                .flag(Constants.COMMON_ONE)
                .build();
        BeanUtil.copyProperties(addBusinessParam, businessDO, CopyOptions.create().ignoreNullValue());
        try {
            businessDO.setBusinessAllNamePY(PinyinUtils.toPinYinLowercase(addBusinessParam.getBusinessAllName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, tip + "全称转换拼音失败!");
        }
        try {
            businessDO.setBusinessShortNamePY(PinyinUtils.toPinYinLowercase(addBusinessParam.getBusinessShortName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, tip + "简称转换拼音失败!");
        }
        //处理单位编号
        switch (addBusinessParam.getBusinessType()) {
            case 1:
                //目的地编号2开头6位编号
                businessDO.setBusinessNO(CodeConstants.COMMON_TWO + numberMachineUtils.getTableNO(NumberMachineConstants.ORG_BUSINESS_NO_SEQ, 5));
                break;
            case 2:
            case 3:
                //旅行社和分销商平台编号3开头6位编号
                businessDO.setBusinessNO(CodeConstants.COMMON_THREE + numberMachineUtils.getTableNO(NumberMachineConstants.ORG_BUSINESS_NO_SEQ, 5));
                break;
            default:
                break;
        }
        baseApplicationDOList.add(businessDO);
        //生成单位系统管理员
        UserDO userDO = UserDO.builder()
                .userId(numberMachineUtils.getTableID(NumberMachineConstants.USER_TABLE_ID_SEQ))
                .businessId(businessDO.getBusinessId())
                .businessType(addBusinessParam.getBusinessType())
                .cellphone(addBusinessParam.getContactPhoneNo())
                //默认启用
                .flag(Constants.COMMON_ONE)
                //系统管理员标识
                .userIdentity(CodeConstants.USER_IDENTITY_XTGLY)
                .password(passwordEncoder.encode(addBusinessParam.getPassword()))
                .userRealName(addBusinessParam.getContactName())
                .userName(addBusinessParam.getUserName())
                .validTime(addBusinessParam.getValidTime())
                .build();
        baseApplicationDOList.add(userDO);
        //生成单位系统管理员角色
        RoleDO roleDO = RoleDO.builder()
                .roleId(numberMachineUtils.getTableID(NumberMachineConstants.ROLE_TABLE_ID_SEQ))
                .businessId(businessDO.getBusinessId())
                .roleName("系统管理员")
                //系统管理员标识
                .rolePid(0L)
                .build();
        baseApplicationDOList.add(roleDO);
        //生成单位系统管理员角色绑定关系
        RoleJoinUserDO roleJoinUserDO = RoleJoinUserDO.builder()
                .roleJoinUserId(numberMachineUtils.getTableID(NumberMachineConstants.ROLE_JOIN_USER_TABLE_ID_SEQ))
                .businessId(businessDO.getBusinessId())
                .roleId(roleDO.getRoleId())
                .userId(userDO.getUserId())
                .build();
        baseApplicationDOList.add(roleJoinUserDO);
        businessService.addBusiness(baseApplicationDOList);
        return returnSuccess("添加" + tip + "成功!");
    }

    /**
     * 添加单位参数校验
     *
     * @param addBusinessParam
     * @author wxc
     * @date 2021/8/9 19:26
     */
    private String checkAddBusinessParam(AddBusinessParam addBusinessParam) {
        if (addBusinessParam == null || addBusinessParam.getBusinessType() == null) {
            return "请选择单位类型!";
        }
        //处理提示消息
        String tip = switchTip(addBusinessParam.getBusinessType());
        if (StringUtils.isEmpty(addBusinessParam.getBusinessAllName())) {
            return "请输入" + tip + "全称!";
        }
        if (StringUtils.isEmpty(addBusinessParam.getBusinessShortName())) {
            return "请输入" + tip + "简称!";
        }

        if (!StringUtils.isEmpty(addBusinessParam.getContactPhoneNo())) {
            if (!ValidatorUtils.checkMobilPhone(addBusinessParam.getContactPhoneNo())) {
                return "【" + addBusinessParam.getContactPhoneNo() + "】手机号码不合法!";
            }
        }
        if (!StringUtils.isEmpty(addBusinessParam.getServiceTel())) {
            if (!ValidatorUtils.checkMobilPhone(addBusinessParam.getServiceTel())) {
                return "【" + addBusinessParam.getServiceTel() + "】客服手机不合法!";
            }
        }
        if (StringUtils.isEmpty(addBusinessParam.getContactName())) {
            return "请输入系统管理员!";
        }
        if (StringUtils.isEmpty(addBusinessParam.getValidTime())) {
            return "请选择有效期限!";
        }
        //目的地
        if (Constants.COMMON_ONE.equals(addBusinessParam.getBusinessType())) {
            if (addBusinessParam.getDestinationType() == null) {
                return "请选择目的地类型!";
            }
        }
        //分销商
        if (Constants.COMMON_TWO.equals(addBusinessParam.getBusinessType())) {
            if (StringUtils.isEmpty(addBusinessParam.getWebsiteName())) {
                return "请填写网站名称!";
            }
            if (StringUtils.isEmpty(addBusinessParam.getWebsiteAddress())) {
                return "请填写网站网址!";
            }
        }
        if (StringUtils.isEmpty(addBusinessParam.getUserName())) {
            return "请输入登录账号!";
        }
        if (StringUtils.isEmpty(addBusinessParam.getPassword())) {
            return "请输入密码!";
        }
        //验证登录账户是否存在
        if (userService.checkUserName(addBusinessParam.getUserName())) {
            return "登录账号已存在!";
        }
        if (businessService.checkAllNameIfExist(addBusinessParam.getBusinessAllName(), null)) {
            return tip + "全称已存在!";
        }
        if (businessService.checkShortNameIfExist(addBusinessParam.getBusinessShortName(), null)) {
            return tip + "简称已存在!";
        }
        return null;
    }

    /**
     * 通过id查询单位
     *
     * @param queryBusinessParam
     * @author wxc
     * @date 2021/7/23 10:03
     */
    @RequestMapping("queryBusinessById")
    @CommonBusiness(logRemark = "根据id查询单位")
   //@PreAuthorize("hasAuthority('businessAction:queryBusinessById')")
    public Object queryBusinessById(@RequestBody(required = false) QueryBusinessParam queryBusinessParam) {

        if (queryBusinessParam == null || queryBusinessParam.getBusinessType() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择单位类型!");
        }
        //处理提示消息
        String tip = switchTip(queryBusinessParam.getBusinessType());
        if (queryBusinessParam.getBusinessId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择查询" + tip + "!");
        }
        BusinessDO businessDO = businessService.queryBusinessById(queryBusinessParam.getBusinessId());
        if (businessDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到" + tip + "信息!");
        }
        //-------------待修改
        QueryBusinessResult queryBusinessResult = QueryBusinessResult.builder().build();
        BeanUtil.copyProperties(businessDO, queryBusinessResult, CopyOptions.create().ignoreNullValue());
        return returnSuccess("查询" + tip + "成功!", queryBusinessResult);
    }

    /**
     * 修改单位
     *
     * @param updateBusinessParam
     * @author wxc
     * @date 2021/7/23 10:04
     */
    @RequestMapping("updateBusiness")
    @CommonBusiness(logRemark = "修改单位")
   //@PreAuthorize("hasAuthority('businessAction:updateBusiness')")
    public Object updateBusiness(@RequestBody(required = false) UpdateBusinessParam updateBusinessParam) {
        String resultError = checkUpdateBusinessParam(updateBusinessParam);
        if (!StringUtils.isEmpty(resultError)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, resultError);
        }
        BusinessDO businessDO = businessService.queryBusinessById(updateBusinessParam.getBusinessId());
        //处理提示消息
        String tip = switchTip(updateBusinessParam.getBusinessType());
        if (businessDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到" + tip + "信息!");
        }
        BeanUtil.copyProperties(updateBusinessParam, businessDO, CopyOptions.create().ignoreNullValue());
        try {
            businessDO.setBusinessAllNamePY(PinyinUtils.toPinYinLowercase(updateBusinessParam.getBusinessAllName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, tip + "全称转换拼音失败!");
        }
        try {
            businessDO.setBusinessShortNamePY(PinyinUtils.toPinYinLowercase(updateBusinessParam.getBusinessShortName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, tip + "简称转换拼音失败!");
        }
        //-------------待修改
        businessDO.setAreaNameStr("北京市");
        businessService.updateBusiness(businessDO);
        return returnSuccess("修改" + tip + "成功!");
    }

    /**
     * 修改单位参数校验
     *
     * @param updateBusinessParam
     * @author wxc
     * @date 2021/8/10 9:15
     */
    private String checkUpdateBusinessParam(UpdateBusinessParam updateBusinessParam) {

        if (updateBusinessParam == null || updateBusinessParam.getBusinessType() == null) {
            return "请选择单位类型!";
        }
        //处理提示消息
        String tip = switchTip(updateBusinessParam.getBusinessType());
        if (updateBusinessParam.getBusinessId() == null) {
            return "请选择修改" + tip + "!";
        }
        if (StringUtils.isEmpty(updateBusinessParam.getBusinessAllName())) {
            return "请输入" + tip + "全称!";
        }
        if (StringUtils.isEmpty(updateBusinessParam.getBusinessShortName())) {
            return "请输入" + tip + "简称!";
        }
        //目的地
        if (Constants.COMMON_ONE.equals(updateBusinessParam.getBusinessType())) {
            if (updateBusinessParam.getDestinationType() == null) {
                return "请选择目的地类型!";
            }
        }
        //分销商
        if (Constants.COMMON_TWO.equals(updateBusinessParam.getBusinessType())) {
            if (StringUtils.isEmpty(updateBusinessParam.getWebsiteName())) {
                return "请填写网站名称!";
            }
            if (StringUtils.isEmpty(updateBusinessParam.getWebsiteAddress())) {
                return "请填写网站网址!";
            }
        }
        if (!StringUtils.isEmpty(updateBusinessParam.getContactPhoneNo())) {
            if (!ValidatorUtils.checkMobilPhone(updateBusinessParam.getContactPhoneNo())) {
                return "【" + updateBusinessParam.getContactPhoneNo() + "】手机号码不合法!";
            }
        }
        if (!StringUtils.isEmpty(updateBusinessParam.getServiceTel())) {
            if (!ValidatorUtils.checkMobilPhone(updateBusinessParam.getServiceTel())) {
                return "【" + updateBusinessParam.getServiceTel() + "】客服手机不合法!";
            }
        }
        if (StringUtils.isEmpty(updateBusinessParam.getContactName())) {
            return "请输入系统管理员!";
        }
        if (StringUtils.isEmpty(updateBusinessParam.getValidTime())) {
            return "请选择有效期限!";
        }
        if (businessService.checkAllNameIfExist(updateBusinessParam.getBusinessAllName(), updateBusinessParam.getBusinessId())) {
            return tip + "全称已存在!";
        }
        if (businessService.checkShortNameIfExist(updateBusinessParam.getBusinessShortName(), updateBusinessParam.getBusinessId())) {
            return tip + "简称已存在!";
        }
        return null;
    }

    /**
     * 重置密码
     *
     * @param resetPasswordParam
     * @author wxc
     * @date 2021/7/24 9:38
     */
    @RequestMapping("resetPassword")
    @CommonBusiness(logRemark = "重置密码")
   //@PreAuthorize("hasAuthority('businessAction:resetPassword')")
    public Object resetPassword(@RequestBody(required = false) ResetPasswordParam resetPasswordParam) {
        if (resetPasswordParam == null || resetPasswordParam.getBusinessType() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择单位类型!");
        }
        String tip = switchTip(resetPasswordParam.getBusinessType());
        if (resetPasswordParam.getBusinessId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择" + tip + "!");
        }
        if (StringUtils.isEmpty(resetPasswordParam.getPassword())) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请生成新密码!");
        }
        BusinessDO businessDO = businessService.queryBusinessById(resetPasswordParam.getBusinessId());
        if (businessDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到" + tip + "信息!");
        }
        resetPasswordParam.setUserName(businessDO.getUserName());
        //密码加密
        resetPasswordParam.setPassword(passwordEncoder.encode(resetPasswordParam.getPassword()));
        businessService.resetPassword(resetPasswordParam);
        return returnSuccess("重置密码成功!");
    }

    /**
     * 修改状态
     *
     * @param modifyStatusParam
     * @author wxc
     * @date 2021/7/24 9:58
     */
    @RequestMapping("modifyStatus")
    @CommonBusiness(logRemark = "修改状态")
   //@PreAuthorize("hasAuthority('businessAction:modifyStatus')")
    public Object modifyStatus(@RequestBody(required = false) ModifyStatusParam modifyStatusParam) {
        if (modifyStatusParam == null || modifyStatusParam.getBusinessType() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择单位类型!");
        }
        //处理提示消息
        String tip = switchTip(modifyStatusParam.getBusinessType());
        if (modifyStatusParam.getBusinessId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择" + tip + "!");
        }
        BusinessDO businessDO = businessService.queryBusinessById(modifyStatusParam.getBusinessId());
        if (businessDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到" + tip + "信息!");
        }
        if (Constants.COMMON_ONE.equals(businessDO.getFlag())) {
            modifyStatusParam.setFlag(Constants.COMMON_TWO);
        } else {
            modifyStatusParam.setFlag(Constants.COMMON_ONE);
        }
        modifyStatusParam.setUserName(businessDO.getUserName());
        businessService.modifyStatus(modifyStatusParam);
        return returnSuccess("状态修改成功!");
    }


    /**
     * 删除单位
     *
     * @param deleteBusinessParam
     * @author wxc
     * @date 2021/7/23 10:04
     */
    @RequestMapping("deleteBusiness")
    @CommonBusiness(logRemark = "删除单位")
   //@PreAuthorize("hasAuthority('businessAction:deleteBusiness')")
    public Object deleteBusiness(@RequestBody(required = false) DeleteBusinessParam deleteBusinessParam) {
        if (deleteBusinessParam == null || deleteBusinessParam.getBusinessType() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择单位类型!");
        }
        //处理提示消息
        String tip = switchTip(deleteBusinessParam.getBusinessType());
        if (deleteBusinessParam.getBusinessId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "请选择" + tip + "!");
        }
        BusinessDO businessDO = businessService.queryBusinessById(deleteBusinessParam.getBusinessId());
        if (businessDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "未查询到" + tip + "信息!");
        }
        if (Constants.COMMON_ZERO.equals(businessDO.getBusinessType())) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "运营商不能删除!");
        }
        businessService.deleteBusiness(deleteBusinessParam.getBusinessId());
        return returnSuccess("删除" + tip + "成功!");
    }


    /**
     * 修改提示消息
     *
     * @param businessType
     * @author wxc
     * @date 2021/7/23 11:40
     */
    private String switchTip(Integer businessType) {
        if (businessType != null) {
            switch (businessType) {
                case 0:
                    return "运营商";
                case 1:
                    return "目的地";
                case 2:
                    return "分销商平台";
                case 3:
                    return "旅行社";
                default:
                    return "单位";
            }
        }
        return "单位";
    }
}
