package com.demo.base.provinceManager.po;

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
 *省份DO
 *
 * @author:kj
 * @date:2021-08-20 16:54
 */
@Entity
@Table(name = "pub_province_t")
@DynamicUpdate
@DynamicInsert
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProvinceDO extends BaseApplicationDO {
    /**
     * 省份编号
     */
    @Id
    @Column(name = "provinceId")
    private Long provinceId;

    /**
     * 省份名称
     */
    @Column(name = "provinceName")
    private String provinceName;

    /**
     * 省份首字母缩写
     */
    @Column(name = "provinceChar")
    private String provinceChar;

    /**
     * 省份身份证编号
     */
    @Column(name = "certificateNo")
    private String certificateNo;

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

    /**
     * 删除标记，codeName：DELETE_MARK(0:未删除，1：已删除)
     */
    @Column(name = "markDelete")
    private Integer markDelete;

}
