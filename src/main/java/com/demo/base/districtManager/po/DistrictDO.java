package com.demo.base.districtManager.po;

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
 * @author kj
 * @date 2021/8/13 14:44
 */
@Entity
@Data
@Builder
@Table(name = "pub_district_t")
@DynamicUpdate(true)
@DynamicInsert(true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class DistrictDO extends BaseApplicationDO {

    /**
     * 县区编号
     */
    @Id
    @Column(name = "districtId")
    private Long districtId;

    /**
     * 县区名称
     */
    @Column(name = "districtName")
    private String districtName;

    /**
     * 县区首字母缩写
     */
    @Column(name = "districtChar")
    private String districtChar;

    /**
     * 城市编号
     */
    @Column(name = "cityId")
    private Long cityId;

    /**
     * 地区身份证编号
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
