package com.demo.base.userManager.po;

import com.demo.dbutils.BaseApplicationDO;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户管理 DO
 * @author sw
 * @date 2021-06-13 09:36:21
 */
@Entity
@Table(name = "sys_user_t")
@DynamicUpdate
@DynamicInsert
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class UserDO extends BaseApplicationDO {

    /**
     * 用户ID
     */
    @Id
    @Column(name = "userId")
    private Long userId;

    /**
     * 用户登录名
     */
    @Column(name = "userName")
    private String userName;

    /**
     * 用户真实姓名
     */
    @Column(name = "userRealName")
    private String userRealName;

    /**
     * 性别 CodeName：GENDER_TYPE 1：男 2：女
     */
    @Column(name = "gender")
    private Integer gender;

    /**
     * 用户身份codeName=''USER_IDENTITY'' 1:超级管理员
     * 2:系统管理员
     * 3:普通用户
     */
    @Column(name = "userIdentity")
    private Integer userIdentity;

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
     * 单位id
     */
    @Column(name = "businessId")
    private Long businessId;

    /**
     * 最后登录IP
     */
    @Column(name = "lastIp")
    private String lastIp;

    /**
     * 最后登录时间
     */
    @Column(name = "lastTime")
    private Date lastTime;

    /**
     * 用户密码
     */
    @Column(name = "password")
    private String password;

    /**
     * 手机号码
     */
    @Column(name = "cellphone")
    private String cellphone;

    /**
     * 最后一次修改密码时间
     */
    @Column(name = "lastChangePasswordTime")
    private Date lastChangePasswordTime;

    /**
     * 最后一次锁定时间
     */
    @Column(name = "lastLockTime")
    private Date lastLockTime;

    /**
     * 失败次数
     */
    @Column(name = "failCount")
    private Integer failCount;

    /**
     * 有效期限，格式：开始日期-结束日期
     *-1表示无限期
     */
    @Column(name = "validTime")
    private String validTime;

    /**
     * 标记 (1:启用 2:停用  默认 1 )CodeName：ENABLE_DISABLED
     */
    @Column(name = "flag")
    private Integer flag;

    /**
     * 用户类型ID,sys_user_type_t同名字段
     */
    @Column(name = "userTypeId")
    private Long userTypeId;

}
