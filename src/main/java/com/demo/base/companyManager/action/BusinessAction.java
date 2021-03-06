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
import com.demo.cache.QueryCacheUtils;
import com.demo.contants.CodeConstants;
import com.demo.contants.Constants;
import com.demo.contants.NumberMachineConstants;
import com.demo.contants.PropertyConstants;
import com.demo.dbutils.BaseApplicationDO;
import com.demo.system.codeManager.dto.CodeDTO;
import com.demo.system.propertyManager.dto.PropertyDTO;
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
 * ????????????action
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
    @Autowired
    private QueryCacheUtils queryCacheUtils;

    /**
     * ??????????????????
     *
     * @param findBusinessParam
     * @author wxc
     * @date 2021/7/23 10:03
     */
    @RequestMapping("findBusinessList")
    @CommonBusiness(logRemark = "??????????????????")
   @PreAuthorize("hasAuthority('businessAction:findBusinessList')")
    public Object findBusinessList(@RequestBody(required = false) FindBusinessParam findBusinessParam) {
        if (findBusinessParam == null || findBusinessParam.getBusinessType() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????????????????!");
        }
        if (Constants.COMMON_ZERO.equals(findBusinessParam.getBusinessType())) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????????????????!");
        }
        String tip = switchTip(findBusinessParam.getBusinessType());
        //?????????????????????????????????????????????????????????
        if (!Constants.COMMON_ONE.equals(findBusinessParam.getBusinessType())) {
            findBusinessParam.setDestinationType(null);
        }
        QueryPage queryPage = initQueryPage(findBusinessParam);
        List<BusinessDTO> businessDTOList = businessService.findBusinessList(findBusinessParam, queryPage);
        List<FindBusinessResult> findBusinessResultList = processBusinessInfo(businessDTOList);
        return returnSuccessListByPage(findBusinessResultList, queryPage, "??????" + tip + "????????????!");
    }

    /**
     * ???????????????????????????
     *
     * @param findBusinessSelectParam
     * @author wxc
     * @date 2021/8/2 14:23
     */
    @RequestMapping("findBusinessSelect")
    @CommonBusiness(logRemark = "???????????????????????????")
   @PreAuthorize("hasAuthority('businessAction:findBusinessSelect')")
    public Object findBusinessSelect(@RequestBody(required = false) FindBusinessSelectParam findBusinessSelectParam) {
        if (findBusinessSelectParam == null || findBusinessSelectParam.getBusinessType() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????????????????!");
        }
        if (Constants.COMMON_ZERO.equals(findBusinessSelectParam.getBusinessType())) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????????????????!");
        }
        return returnSuccess("????????????????????????????????????", businessService.findBusinessSelect(findBusinessSelectParam));
    }


    /**
     * ????????????????????????
     *
     * @param findMerchantUserParam
     * @author wxc
     * @date 2021/7/29 13:56
     */
    @RequestMapping("findBusinessUserList")
    @CommonBusiness(logRemark = "????????????????????????")
   @PreAuthorize("hasAuthority('businessAction:findBusinessUserList')")
    public Object findBusinessUserList(@RequestBody(required = false) FindMerchantUserParam findMerchantUserParam) {
        if (findMerchantUserParam == null) {
            findMerchantUserParam = FindMerchantUserParam.builder().build();
        }
        if (Constants.COMMON_ZERO.equals(findMerchantUserParam.getBusinessType())) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????????????????!");
        }
        QueryPage queryPage = initQueryPage(findMerchantUserParam);
        List<BusinessDTO> businessDTOList = businessService.findBusinessUserList(findMerchantUserParam, queryPage);
        List<FindBusinessResult> findBusinessResultList = processBusinessInfo(businessDTOList);
        return returnSuccessListByPage(findBusinessResultList, queryPage, "??????????????????????????????!");
    }

    /**
     * ??????????????????
     *
     * @param businessDTOList
     * @author wxc
     * @date 2021/8/9 18:39
     */
    private List<FindBusinessResult> processBusinessInfo(List<BusinessDTO> businessDTOList) {
        return businessDTOList.stream().map(businessDTO -> {
            FindBusinessResult findBusinessResult = FindBusinessResult.builder().build();
            BeanUtil.copyProperties(businessDTO, findBusinessResult, CopyOptions.create().ignoreNullValue());
            findBusinessResult.setValidTime("-1".equals(businessDTO.getValidTime()) ? "?????????" : businessDTO.getValidTime().replace(",", "???"));
            return findBusinessResult;
        }).collect(Collectors.toList());
    }

    /**
     * ????????????????????????
     *
     * @param queryBusinessParam
     * @author wxc
     * @date 2021/7/29 13:57
     */
    @RequestMapping("findBusinessUserInfo")
    @CommonBusiness(logRemark = "????????????????????????")
   @PreAuthorize("hasAuthority('businessAction:findBusinessUserInfo')")
    public Object findBusinessUserInfo(@RequestBody(required = false) QueryBusinessParam queryBusinessParam) {
        if (queryBusinessParam == null || queryBusinessParam.getBusinessId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????????????????!");
        }
        BusinessDTO businessDTO = businessService.findBusinessUserInfo(queryBusinessParam.getBusinessId());
        businessDTO.setValidTime("-1".equals(businessDTO.getValidTime()) ? "?????????" : businessDTO.getValidTime().replace(",", "???"));
        return returnSuccess("??????????????????????????????!", businessDTO);
    }


    /**
     * ???????????????
     *
     * @param
     * @author wxc
     * @date 2021/7/24 9:29
     */
    @RequestMapping("findOperation")
    @CommonBusiness(logRemark = "???????????????")
   @PreAuthorize("hasAuthority('businessAction:findOperation')")
    public Object findOperation() {
        PropertyDTO propertyDTO = queryCacheUtils.queryCachePropertyByPropKey(PropertyConstants.FDFS_WEB_SERVER_URL);
        if (propertyDTO == null || StringUtils.isEmpty(propertyDTO.getPropValue())) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "??????????????????????????????!");
        }
        BusinessDTO operation = businessService.findOperation();
        if (operation != null) {
            operation.setAbMallLogo(propertyDTO.getPropValue() + operation.getMallLogo());
            operation.setAbPlatformLogo(propertyDTO.getPropValue() + operation.getPlatformLogo());
        }
        return returnSuccess("?????????????????????!", operation);
    }

    /**
     * ??????????????????
     *
     * @param operateGroupBusinessParam
     * @author wxc
     * @date 2021/7/29 9:50
     */
    @RequestMapping("operateGroupBusiness")
    @CommonBusiness(logRemark = "??????????????????")
   @PreAuthorize("hasAuthority('businessAction:operateGroupBusiness')")
    public Object operateGroupBusiness(@RequestBody(required = false) OperateGroupBusinessParam operateGroupBusinessParam) {
        if (operateGroupBusinessParam == null || operateGroupBusinessParam.getJurisGroupId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "???????????????!");
        }
        List<Long> businessIdList = operateGroupBusinessParam.getBusinessIdList();
        if (CollectionUtil.isEmpty(businessIdList)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "???????????????!");
        }
        JurisGroupDO jurisGroupDO = jurisGroupService.queryJurisGroupById(operateGroupBusinessParam.getJurisGroupId());
        if (jurisGroupDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "????????????????????????!");
        }
        if (jurisGroupDO.getJurisGroupPid() == 0L) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "????????????????????????????????????!");
        }
        List<JurisGroupOrgDO> jurisGroupOrgDOList = new ArrayList<>();
        SequenceNumberBizBean sequenceNumberBizBean = numberMachineUtils.getTableIDByCount(NumberMachineConstants.SYS_JURIS_GROUP_ORG_TABLE_ID_SEQ, businessIdList.size());
        businessIdList.forEach(businessId -> jurisGroupOrgDOList.add(JurisGroupOrgDO.builder()
                .jurisGroupId(operateGroupBusinessParam.getJurisGroupId())
                .businessId(businessId)
                .jurisGroupOrgId(numberMachineUtils.getTableIdBySequenceNumber(sequenceNumberBizBean))
                .build()));
        businessService.operateGroupBusiness(jurisGroupOrgDOList);
        return returnSuccess("????????????!");
    }

    /**
     * ????????????????????????
     *
     * @param findGroupBusinessParam
     * @author wxc
     * @date 2021/7/29 9:50
     */
    @RequestMapping("findGroupBusiness")
    @CommonBusiness(logRemark = "????????????????????????")
   @PreAuthorize("hasAuthority('businessAction:findGroupBusiness')")
    public Object findGroupBusiness(@RequestBody(required = false) FindGroupBusinessParam findGroupBusinessParam) {
        if (findGroupBusinessParam == null || findGroupBusinessParam.getJurisGroupId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "???????????????!");
        }
        JurisGroupDO jurisGroupDO = jurisGroupService.queryJurisGroupById(findGroupBusinessParam.getJurisGroupId());
        if (jurisGroupDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "????????????????????????!");
        }
        if (jurisGroupDO.getJurisGroupPid() == 0L) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "???????????????????????????????????????!");
        }
        //????????????????????? ????????????????????????
        if (findGroupBusinessParam.getBusinessType() != null && !findGroupBusinessParam.getBusinessType().equals(Constants.COMMON_ONE)) {
            if (Constants.COMMON_ZERO.equals(findGroupBusinessParam.getBusinessType())) {
                return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????????????????!");
            }
            findGroupBusinessParam.setDestinationType(null);
        }
        QueryPage queryPage = initQueryPage(findGroupBusinessParam);
        List<BusinessDTO> businessDTOList = businessService.findGroupBusiness(findGroupBusinessParam, queryPage);
        return returnSuccessListByPage(businessDTOList, queryPage, "????????????????????????!");
    }

    /**
     * ??????????????????
     *
     * @param deleteGroupBusinessParam
     * @author wxc
     * @date 2021/7/29 9:50
     */
    @RequestMapping("deleteGroupBusiness")
    @CommonBusiness(logRemark = "??????????????????")
   @PreAuthorize("hasAuthority('businessAction:deleteGroupBusiness')")
    public Object deleteGroupBusiness(@RequestBody(required = false) DeleteGroupBusinessParam deleteGroupBusinessParam) {
        if (deleteGroupBusinessParam == null || deleteGroupBusinessParam.getJurisGroupId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "???????????????!");
        }
        if (deleteGroupBusinessParam.getBusinessId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "???????????????!");
        }
        businessService.deleteGroupBusiness(deleteGroupBusinessParam);
        return returnSuccess("????????????????????????!");
    }

    /**
     * ???????????????????????????
     *
     * @param findGroupBusinessParam
     * @author wxc
     * @date 2021/7/29 9:51
     */
    @RequestMapping("findNeverInGroupBusiness")
    @CommonBusiness(logRemark = "???????????????????????????")
   @PreAuthorize("hasAuthority('businessAction:findNeverInGroupBusiness')")
    public Object findNeverInGroupBusiness(@RequestBody(required = false) FindGroupBusinessParam findGroupBusinessParam) {
        if (findGroupBusinessParam == null) {
            findGroupBusinessParam = FindGroupBusinessParam.builder().build();
        }
        QueryPage queryPage = initQueryPage(findGroupBusinessParam);
        //????????????????????? ????????????????????????
        if (findGroupBusinessParam.getBusinessType() != null && !findGroupBusinessParam.getBusinessType().equals(Constants.COMMON_ONE)) {
            if (Constants.COMMON_ZERO.equals(findGroupBusinessParam.getBusinessType())) {
                return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????????????????!");
            }
            findGroupBusinessParam.setDestinationType(null);
        }
        List<BusinessDTO> businessDTOList = businessService.findNeverInGroupBusiness(findGroupBusinessParam, queryPage);
        return returnSuccessListByPage(businessDTOList, queryPage, "?????????????????????????????????!");
    }


    /**
     * ???????????????
     *
     * @param modifyOperationParam
     * @author wxc
     * @date 2021/7/24 9:29
     */
    @RequestMapping("modifyOperation")
    @CommonBusiness(logRemark = "???????????????")
   @PreAuthorize("hasAuthority('businessAction:modifyOperation')")
    public Object modifyOperation(@RequestBody(required = false) ModifyOperationParam modifyOperationParam) {
        String checkResult = checkModifyOperation(modifyOperationParam);
        if (StringUtils.isNotBlank(checkResult)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, checkResult);
        }
        BusinessDO businessDO = businessService.queryBusinessById(modifyOperationParam.getBusinessId());
        if (businessDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "???????????????????????????!");
        }
        if (!Constants.COMMON_ZERO.equals(businessDO.getBusinessType())) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "??????????????????????????????!");
        }
        BeanUtil.copyProperties(modifyOperationParam, businessDO, CopyOptions.create().ignoreNullValue());
        try {
            businessDO.setBusinessAllNamePY(PinyinUtils.toPinYinLowercase(modifyOperationParam.getBusinessAllName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????????????????????????????!");
        }
        try {
            businessDO.setBusinessShortNamePY(PinyinUtils.toPinYinLowercase(modifyOperationParam.getBusinessShortName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????????????????????????????!");
        }
        businessService.updateBusiness(businessDO);
        return returnSuccess("?????????????????????!");
    }

    /**
     * ???????????????????????????
     *
     * @param modifyOperationParam
     * @author wxc
     * @date 2021/8/9 19:16
     */
    private String checkModifyOperation(ModifyOperationParam modifyOperationParam) {
        if (modifyOperationParam == null || modifyOperationParam.getBusinessId() == null) {
            return "??????????????????!";
        }

        if (StringUtils.isEmpty(modifyOperationParam.getBusinessAllName())) {
            return "????????????????????????!";
        }

        if (StringUtils.isEmpty(modifyOperationParam.getBusinessShortName())) {
            return "????????????????????????!";
        }

        if (StringUtils.isEmpty(modifyOperationParam.getContactPhoneNo())) {
            return "?????????????????????!";
        }
        if (!ValidatorUtils.checkMobilPhone(modifyOperationParam.getContactPhoneNo())) {
            return "???" + modifyOperationParam.getContactPhoneNo() + "????????????????????????!";
        }
        if (StringUtils.isEmpty(modifyOperationParam.getPlatformName())) {
            return "??????????????????!";
        }
        if (StringUtils.isEmpty(modifyOperationParam.getPlatformLogo())) {
            return "???????????????LOGO!";
        }
        if (StringUtils.isEmpty(modifyOperationParam.getMallName())) {
            return "?????????????????????!";
        }
        if (StringUtils.isEmpty(modifyOperationParam.getMallLogo())) {
            return "???????????????LOGO!";
        }
        return null;
    }


    /**
     * ????????????
     *
     * @param addBusinessParam
     * @author wxc
     * @date 2021/7/23 10:03
     */
    @RequestMapping("addBusiness")
    @CommonBusiness(logRemark = "????????????")
   @PreAuthorize("hasAuthority('businessAction:addBusiness')")
    public Object addBusiness(@RequestBody(required = false) AddBusinessParam addBusinessParam) {
        String checkResult = checkAddBusinessParam(addBusinessParam);
        if (!StringUtils.isEmpty(checkResult)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, checkResult);
        }
        String tip = switchTip(addBusinessParam.getBusinessType());
        List<BaseApplicationDO> baseApplicationDOList = new ArrayList<>();
        BusinessDO businessDO = BusinessDO.builder()
                .businessId(numberMachineUtils.getTableID(NumberMachineConstants.ORG_BUSINESS_TABLE_ID_SEQ))
                //-------------?????????
                .areaNameStr("?????????")
                .flag(Constants.COMMON_ONE)
                .build();
        BeanUtil.copyProperties(addBusinessParam, businessDO, CopyOptions.create().ignoreNullValue());
        try {
            businessDO.setBusinessAllNamePY(PinyinUtils.toPinYinLowercase(addBusinessParam.getBusinessAllName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, tip + "????????????????????????!");
        }
        try {
            businessDO.setBusinessShortNamePY(PinyinUtils.toPinYinLowercase(addBusinessParam.getBusinessShortName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, tip + "????????????????????????!");
        }
        //??????????????????
        switch (addBusinessParam.getBusinessType()) {
            case 1:
                //???????????????2??????6?????????
                businessDO.setBusinessNO(CodeConstants.COMMON_TWO + numberMachineUtils.getTableNO(NumberMachineConstants.ORG_BUSINESS_NO_SEQ, 5));
                break;
            case 2:
            case 3:
                //?????????????????????????????????3??????6?????????
                businessDO.setBusinessNO(CodeConstants.COMMON_THREE + numberMachineUtils.getTableNO(NumberMachineConstants.ORG_BUSINESS_NO_SEQ, 5));
                break;
            default:
                break;
        }
        baseApplicationDOList.add(businessDO);
        //???????????????????????????
        UserDO userDO = UserDO.builder()
                .userId(numberMachineUtils.getTableID(NumberMachineConstants.USER_TABLE_ID_SEQ))
                .businessId(businessDO.getBusinessId())
                .businessType(addBusinessParam.getBusinessType())
                .cellphone(addBusinessParam.getContactPhoneNo())
                //????????????
                .flag(Constants.COMMON_ONE)
                //?????????????????????
                .userIdentity(CodeConstants.USER_IDENTITY_XTGLY)
                .password(passwordEncoder.encode(addBusinessParam.getPassword()))
                .userRealName(addBusinessParam.getContactName())
                .userName(addBusinessParam.getUserName())
                .validTime(addBusinessParam.getValidTime())
                .build();
        baseApplicationDOList.add(userDO);
        //?????????????????????????????????
        RoleDO roleDO = RoleDO.builder()
                .roleId(numberMachineUtils.getTableID(NumberMachineConstants.ROLE_TABLE_ID_SEQ))
                .businessId(businessDO.getBusinessId())
                .roleName("???????????????")
                //?????????????????????
                .rolePid(0L)
                .build();
        baseApplicationDOList.add(roleDO);
        //?????????????????????????????????????????????
        RoleJoinUserDO roleJoinUserDO = RoleJoinUserDO.builder()
                .roleJoinUserId(numberMachineUtils.getTableID(NumberMachineConstants.ROLE_JOIN_USER_TABLE_ID_SEQ))
                .businessId(businessDO.getBusinessId())
                .roleId(roleDO.getRoleId())
                .userId(userDO.getUserId())
                .build();
        baseApplicationDOList.add(roleJoinUserDO);
        businessService.addBusiness(baseApplicationDOList);
        return returnSuccess("??????" + tip + "??????!");
    }

    /**
     * ????????????????????????
     *
     * @param addBusinessParam
     * @author wxc
     * @date 2021/8/9 19:26
     */
    private String checkAddBusinessParam(AddBusinessParam addBusinessParam) {
        if (addBusinessParam == null || addBusinessParam.getBusinessType() == null) {
            return "?????????????????????!";
        }
        //??????????????????
        String tip = switchTip(addBusinessParam.getBusinessType());
        if (StringUtils.isEmpty(addBusinessParam.getBusinessAllName())) {
            return "?????????" + tip + "??????!";
        }
        if (StringUtils.isEmpty(addBusinessParam.getBusinessShortName())) {
            return "?????????" + tip + "??????!";
        }

        if (!StringUtils.isEmpty(addBusinessParam.getContactPhoneNo())) {
            if (!ValidatorUtils.checkMobilPhone(addBusinessParam.getContactPhoneNo())) {
                return "???" + addBusinessParam.getContactPhoneNo() + "????????????????????????!";
            }
        }
        if (!StringUtils.isEmpty(addBusinessParam.getServiceTel())) {
            if (!ValidatorUtils.checkMobilPhone(addBusinessParam.getServiceTel())) {
                return "???" + addBusinessParam.getServiceTel() + "????????????????????????!";
            }
        }
        if (StringUtils.isEmpty(addBusinessParam.getContactName())) {
            return "????????????????????????!";
        }
        if (StringUtils.isEmpty(addBusinessParam.getValidTime())) {
            return "?????????????????????!";
        }
        //?????????
        if (Constants.COMMON_ONE.equals(addBusinessParam.getBusinessType())) {
            if (addBusinessParam.getDestinationType() == null) {
                return "????????????????????????!";
            }
        }
        //?????????
        if (Constants.COMMON_TWO.equals(addBusinessParam.getBusinessType())) {
            if (StringUtils.isEmpty(addBusinessParam.getWebsiteName())) {
                return "?????????????????????!";
            }
            if (StringUtils.isEmpty(addBusinessParam.getWebsiteAddress())) {
                return "?????????????????????!";
            }
        }
        if (StringUtils.isEmpty(addBusinessParam.getUserName())) {
            return "?????????????????????!";
        }
        if (StringUtils.isEmpty(addBusinessParam.getPassword())) {
            return "???????????????!";
        }
        //??????????????????????????????
        if (userService.checkUserName(addBusinessParam.getUserName())) {
            return "?????????????????????!";
        }
        if (businessService.checkAllNameIfExist(addBusinessParam.getBusinessAllName(), null)) {
            return tip + "???????????????!";
        }
        if (businessService.checkShortNameIfExist(addBusinessParam.getBusinessShortName(), null)) {
            return tip + "???????????????!";
        }
        return null;
    }

    /**
     * ??????id????????????
     *
     * @param queryBusinessParam
     * @author wxc
     * @date 2021/7/23 10:03
     */
    @RequestMapping("queryBusinessById")
    @CommonBusiness(logRemark = "??????id????????????")
   @PreAuthorize("hasAuthority('businessAction:queryBusinessById')")
    public Object queryBusinessById(@RequestBody(required = false) QueryBusinessParam queryBusinessParam) {

        if (queryBusinessParam == null || queryBusinessParam.getBusinessType() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????????????????!");
        }
        //??????????????????
        String tip = switchTip(queryBusinessParam.getBusinessType());
        if (queryBusinessParam.getBusinessId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "???????????????" + tip + "!");
        }
        BusinessDO businessDO = businessService.queryBusinessById(queryBusinessParam.getBusinessId());
        if (businessDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "????????????" + tip + "??????!");
        }
        //-------------?????????
        QueryBusinessResult queryBusinessResult = QueryBusinessResult.builder().build();
        BeanUtil.copyProperties(businessDO, queryBusinessResult, CopyOptions.create().ignoreNullValue());
        return returnSuccess("??????" + tip + "??????!", queryBusinessResult);
    }

    /**
     * ????????????
     *
     * @param updateBusinessParam
     * @author wxc
     * @date 2021/7/23 10:04
     */
    @RequestMapping("updateBusiness")
    @CommonBusiness(logRemark = "????????????")
   @PreAuthorize("hasAuthority('businessAction:updateBusiness')")
    public Object updateBusiness(@RequestBody(required = false) UpdateBusinessParam updateBusinessParam) {
        String resultError = checkUpdateBusinessParam(updateBusinessParam);
        if (!StringUtils.isEmpty(resultError)) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, resultError);
        }
        BusinessDO businessDO = businessService.queryBusinessById(updateBusinessParam.getBusinessId());
        //??????????????????
        String tip = switchTip(updateBusinessParam.getBusinessType());
        if (businessDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "????????????" + tip + "??????!");
        }
        BeanUtil.copyProperties(updateBusinessParam, businessDO, CopyOptions.create().ignoreNullValue());
        try {
            businessDO.setBusinessAllNamePY(PinyinUtils.toPinYinLowercase(updateBusinessParam.getBusinessAllName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, tip + "????????????????????????!");
        }
        try {
            businessDO.setBusinessShortNamePY(PinyinUtils.toPinYinLowercase(updateBusinessParam.getBusinessShortName()));
        } catch (Exception e) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, tip + "????????????????????????!");
        }
        //-------------?????????
        businessDO.setAreaNameStr("?????????");
        businessService.updateBusiness(businessDO);
        return returnSuccess("??????" + tip + "??????!");
    }

    /**
     * ????????????????????????
     *
     * @param updateBusinessParam
     * @author wxc
     * @date 2021/8/10 9:15
     */
    private String checkUpdateBusinessParam(UpdateBusinessParam updateBusinessParam) {

        if (updateBusinessParam == null || updateBusinessParam.getBusinessType() == null) {
            return "?????????????????????!";
        }
        //??????????????????
        String tip = switchTip(updateBusinessParam.getBusinessType());
        if (updateBusinessParam.getBusinessId() == null) {
            return "???????????????" + tip + "!";
        }
        if (StringUtils.isEmpty(updateBusinessParam.getBusinessAllName())) {
            return "?????????" + tip + "??????!";
        }
        if (StringUtils.isEmpty(updateBusinessParam.getBusinessShortName())) {
            return "?????????" + tip + "??????!";
        }
        //?????????
        if (Constants.COMMON_ONE.equals(updateBusinessParam.getBusinessType())) {
            if (updateBusinessParam.getDestinationType() == null) {
                return "????????????????????????!";
            }
        }
        //?????????
        if (Constants.COMMON_TWO.equals(updateBusinessParam.getBusinessType())) {
            if (StringUtils.isEmpty(updateBusinessParam.getWebsiteName())) {
                return "?????????????????????!";
            }
            if (StringUtils.isEmpty(updateBusinessParam.getWebsiteAddress())) {
                return "?????????????????????!";
            }
        }
        if (!StringUtils.isEmpty(updateBusinessParam.getContactPhoneNo())) {
            if (!ValidatorUtils.checkMobilPhone(updateBusinessParam.getContactPhoneNo())) {
                return "???" + updateBusinessParam.getContactPhoneNo() + "????????????????????????!";
            }
        }
        if (!StringUtils.isEmpty(updateBusinessParam.getServiceTel())) {
            if (!ValidatorUtils.checkMobilPhone(updateBusinessParam.getServiceTel())) {
                return "???" + updateBusinessParam.getServiceTel() + "????????????????????????!";
            }
        }
        if (StringUtils.isEmpty(updateBusinessParam.getContactName())) {
            return "????????????????????????!";
        }
        if (StringUtils.isEmpty(updateBusinessParam.getValidTime())) {
            return "?????????????????????!";
        }
        if (businessService.checkAllNameIfExist(updateBusinessParam.getBusinessAllName(), updateBusinessParam.getBusinessId())) {
            return tip + "???????????????!";
        }
        if (businessService.checkShortNameIfExist(updateBusinessParam.getBusinessShortName(), updateBusinessParam.getBusinessId())) {
            return tip + "???????????????!";
        }
        return null;
    }

    /**
     * ????????????
     *
     * @param resetPasswordParam
     * @author wxc
     * @date 2021/7/24 9:38
     */
    @RequestMapping("resetPassword")
    @CommonBusiness(logRemark = "????????????")
   @PreAuthorize("hasAuthority('businessAction:resetPassword')")
    public Object resetPassword(@RequestBody(required = false) ResetPasswordParam resetPasswordParam) {
        if (resetPasswordParam == null || resetPasswordParam.getBusinessType() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????????????????!");
        }
        String tip = switchTip(resetPasswordParam.getBusinessType());
        if (resetPasswordParam.getBusinessId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????" + tip + "!");
        }
        if (StringUtils.isEmpty(resetPasswordParam.getPassword())) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "??????????????????!");
        }
        BusinessDO businessDO = businessService.queryBusinessById(resetPasswordParam.getBusinessId());
        if (businessDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "????????????" + tip + "??????!");
        }
        resetPasswordParam.setUserName(businessDO.getUserName());
        //????????????
        resetPasswordParam.setPassword(passwordEncoder.encode(resetPasswordParam.getPassword()));
        businessService.resetPassword(resetPasswordParam);
        return returnSuccess("??????????????????!");
    }

    /**
     * ????????????
     *
     * @param modifyStatusParam
     * @author wxc
     * @date 2021/7/24 9:58
     */
    @RequestMapping("modifyStatus")
    @CommonBusiness(logRemark = "????????????")
   @PreAuthorize("hasAuthority('businessAction:modifyStatus')")
    public Object modifyStatus(@RequestBody(required = false) ModifyStatusParam modifyStatusParam) {
        if (modifyStatusParam == null || modifyStatusParam.getBusinessType() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????????????????!");
        }
        //??????????????????
        String tip = switchTip(modifyStatusParam.getBusinessType());
        if (modifyStatusParam.getBusinessId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????" + tip + "!");
        }
        BusinessDO businessDO = businessService.queryBusinessById(modifyStatusParam.getBusinessId());
        if (businessDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "????????????" + tip + "??????!");
        }
        if (Constants.COMMON_ONE.equals(businessDO.getFlag())) {
            modifyStatusParam.setFlag(Constants.COMMON_TWO);
        } else {
            modifyStatusParam.setFlag(Constants.COMMON_ONE);
        }
        modifyStatusParam.setUserName(businessDO.getUserName());
        businessService.modifyStatus(modifyStatusParam);
        return returnSuccess("??????????????????!");
    }


    /**
     * ????????????
     *
     * @param deleteBusinessParam
     * @author wxc
     * @date 2021/7/23 10:04
     */
    @RequestMapping("deleteBusiness")
    @CommonBusiness(logRemark = "????????????")
   @PreAuthorize("hasAuthority('businessAction:deleteBusiness')")
    public Object deleteBusiness(@RequestBody(required = false) DeleteBusinessParam deleteBusinessParam) {
        if (deleteBusinessParam == null || deleteBusinessParam.getBusinessType() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????????????????!");
        }
        //??????????????????
        String tip = switchTip(deleteBusinessParam.getBusinessType());
        if (deleteBusinessParam.getBusinessId() == null) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????" + tip + "!");
        }
        BusinessDO businessDO = businessService.queryBusinessById(deleteBusinessParam.getBusinessId());
        if (businessDO == null) {
            return returnFail(ResultCode.BIS_DATA_NO_EXIST, "????????????" + tip + "??????!");
        }
        if (Constants.COMMON_ZERO.equals(businessDO.getBusinessType())) {
            return returnFail(ResultCode.AUTH_PARAM_ERROR, "?????????????????????!");
        }
        businessService.deleteBusiness(deleteBusinessParam.getBusinessId());
        return returnSuccess("??????" + tip + "??????!");
    }


    /**
     * ??????????????????
     *
     * @param businessType
     * @author wxc
     * @date 2021/7/23 11:40
     */
    private String switchTip(Integer businessType) {
        if (businessType == null) {
            return "??????";
        }
        List<CodeDTO> codeDTOList = queryCacheUtils.queryCacheCodeByCodeName(CodeConstants.BUSINESS_TYPE);
        if (CollectionUtil.isEmpty(codeDTOList)) {
            return "??????";
        }
        List<CodeDTO> codeDTOS = codeDTOList.stream().filter(i -> businessType.equals(Integer.parseInt(i.getCodeKey()))).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(codeDTOS)) {
            return "??????";
        }
        return codeDTOS.get(0).getCodeValue();
    }
}
