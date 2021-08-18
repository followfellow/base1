package com.demo.base.companyManager.po;

import com.demo.dbutils.BaseApplicationDO;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 单位DO
 *
 * @author:wxc
 * @date:2021/7/22 15:49
 */
@Entity
@Table(name = "org_business_t")
@DynamicUpdate
@DynamicInsert
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class BusinessDO extends BaseApplicationDO {
    /**
     * 单位信息ID
     */
    @Id
    @Column(name = "businessId")
    private Long businessId;

    /**
     * 单位名称-全称
     */
    @Column(name = "businessAllName")
    private String businessAllName;

    /**
     * 单位名称-全称拼音
     */
    @Column(name = "businessAllNamePY")
    private String businessAllNamePY;

    /**
     * 单位名称-简称
     */
    @Column(name = "businessShortName")
    private String businessShortName;

    /**
     * 单位名称-简称拼音
     */
    @Column(name = "businessShortNamePY")
    private String businessShortNamePY;

    /**
     * 单位类型，BUSINESS_TYPE
     * 0：运营商
     * 1：目的地
     * 2：分销商
     * 3：旅行社
     */
    @Column(name = "businessType")
    private Integer businessType;

    /**
     * 目的地类型,CodeName=DESTINATION_TYPE
     * 0：旅游景区
     * 1：宾馆酒店
     * 2：餐饮店铺
     * 3：零售店铺
     * 4：商业管理
     */
    @Column(name = "destinationType")
    private Integer destinationType;

    /**
     * 单位编号
     */
    @Column(name = "businessNO")
    private String businessNO;

    /**
     * 单位等级，文本输入
     */
    @Column(name = "businessLevel")
    private String businessLevel;

    /**
     * 分销商网站名称
     */
    @Column(name = "websiteName")
    private String websiteName;

    /**
     * 分销商网站地址
     */
    @Column(name = "websiteAddress")
    private String websiteAddress;

    /**
     * 国家表主键ID 对应表pub_country_t同名字段
     */
    @Column(name = "countryId")
    private Long countryId;

    /**
     * 省份编号ID，对应pub_province_t表同名字段
     */
    @Column(name = "provinceId")
    private Long provinceId;

    /**
     * 城市ID,对应表pub_city_t同名字段
     */
    @Column(name = "cityId")
    private Long cityId;

    /**
     * 县区编号ID，对应表pub_district_t同名字段
     */
    @Column(name = "districtId")
    private Long districtId;

    /**
     * 所属地区字符串，用于界面展现，通过省市区对应的中文进行拼音，如：浙江省杭州市余杭区
     */
    @Column(name = "areaNameStr")
    private String areaNameStr;

    /**
     * 单位地址
     */
    @Column(name = "businessAddress")
    private String businessAddress;

    /**
     * 单位管理员
     */
    @Column(name = "contactName")
    private String contactName;

    /**
     * 手机号码
     */
    @Column(name = "contactPhoneNo")
    private String contactPhoneNo;

    /**
     * 客服电话
     */
    @Column(name = "serviceTel")
    private String serviceTel;

    /**
     * 电子邮箱
     */
    @Column(name = "email")
    private String email;

    /**
     * 登录账户，对应表“sys_user_t”中的同名字段
     */
    @Column(name = "userName")
    private String userName;

    /**
     * 用户数量
     */
    @Column(name = "userCount")
    private Integer userCount;

    /**
     * 单位有效限制，-1表示无限期，2021-01-01至2021-12-31
     */
    @Column(name = "validTime")
    private String validTime;

    /**
     * 标记 (1:启用 2:停用  默认 1 )CodeName：ENABLE_DISABLED
     */
    @Column(name = "flag")
    private Integer flag;

    /**
     * 平台名称
     */
    @Column(name = "platformName")
    private String platformName;

    /**
     * 平台图标地址
     */
    @Column(name = "platformLogo")
    private String platformLogo;

    /**
     * 商城名称
     */
    @Column(name = "mallName")
    private String mallName;

    /**
     * 商城图标地址
     */
    @Column(name = "mallLogo")
    private String mallLogo;

    /**
     * 创建时间
     */
    @Column(name = "createdDate")
    private Date createdDate;

    /**
     * 创建用户
     */
    @Column(name = "createdUser")
    private String createdUser;

    /**
     * 创建用户id
     */
    @Column(name = "createdUserId")
    private Long createdUserId;

    /**
     * 修改时间
     */
    @Column(name = "updatedDate")
    private Date updatedDate;

    /**
     * 修改用户
     */
    @Column(name = "updatedUser")
    private String updatedUser;

    /**
     * 修改用户id
     */
    @Column(name = "updateUserId")
    private Long updateUserId;

}
